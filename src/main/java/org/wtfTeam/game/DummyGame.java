package org.wtfTeam.game;

//import com.sun.org.glassfish.gmbal.GmbalException;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

import org.wtfTeam.engine.*;
import org.wtfTeam.engine.graph.lights.DirectionalLight;
import org.wtfTeam.engine.items.GameItem;
import org.wtfTeam.engine.graph.*;
import org.wtfTeam.engine.items.SkyBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DummyGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private Scene scene;

    private float lightAngle;

    private int dpi;

    private static final float CAMERA_POS_STEP = 0.05f;

    private static final float SELECT_SENSITIVITY = 0.5f;

    private static final float LIMIT_SELECT = 2.5f;

    private static final float jump_initial_speed = 3.0f;
    private static final float move_max_speed = 1.2f;
    private static final float move_acc = 10f;
    private static final float grav_acc = -9.0f;
    private static final float frac_acc = -5.0f;

    private static boolean flying;
    private static int movx, movz, movy;
    private static boolean f_pressed;

    private ArrayList<Mesh> meshs;
    
    private Map map;

    private Hud hud;

    private long lasPut = 0, lasDig = 0;
    
    private int pastkeyCode = -1;
    
    private int curblock = 0;
    
    public final Block selectSetBlock() {
        Vector3f selectPosition = new Vector3f(camera.getPosition());

        for (float dist = SELECT_SENSITIVITY * 2; dist <= LIMIT_SELECT; dist += SELECT_SENSITIVITY * 2) {
            selectPosition.add(camera.getFront().normalize().mul(SELECT_SENSITIVITY * 2));
            Block block = map.getBlock((int) (Math.rint(selectPosition.x)), (int) (Math.rint(selectPosition.y)), (int) (Math.rint(selectPosition.z)));
            if (block == null) {
                System.out.printf(" %f : %f %f %f\n", dist, selectPosition.x, selectPosition.y, selectPosition.z);
                System.out.printf(" %f : %d %d %d\n", dist, (int) (Math.rint(selectPosition.x)), (int) (Math.rint(selectPosition.y)), (int) (Math.rint(selectPosition.z)));
                return new Block((int) (Math.rint(selectPosition.x)), (int) (Math.rint(selectPosition.y)), (int) (Math.rint(selectPosition.z)), 0);
            }
        }
        return null;
    }

    public final Block selectBlock() {
        Vector3f selectPosition = new Vector3f(camera.getPosition());

        for (float dist = SELECT_SENSITIVITY; dist <= LIMIT_SELECT; dist += SELECT_SENSITIVITY) {
            selectPosition.add(camera.getFront().normalize().mul(SELECT_SENSITIVITY));
            Block block = map.getBlock((int) (Math.rint(selectPosition.x)), (int) (Math.rint(selectPosition.y)), (int) (Math.rint(selectPosition.z)));
            System.out.printf("getBlock: %f %f %f\n", selectPosition.x, selectPosition.y, selectPosition.z);
            System.out.printf("getBlock: %d %d %d\n", (int) (Math.rint(selectPosition.x)), (int) (Math.rint(selectPosition.y)), (int) (Math.rint(selectPosition.z)));
            if (block != null) {
                System.out.println("!");
                return block;
            }
        }
        return null;
    }

    public DummyGame() {
        renderer = new Renderer();
        Vector3f CameraPosition = new Vector3f(0f, 15f, 0f);
        Vector3f CameraRo = new Vector3f(-90, 180, 0);
        camera = new Camera(CameraPosition, CameraRo);
        cameraInc = new Vector3f(0, 0, 0);//鐓х浉鏈洪�熷害
        dpi = 5;
        lightAngle = -90;
        flying = false;
        f_pressed = false;

        meshs = new ArrayList<>();
    }
    
    public DummyGame(float fx, float fy, float fz) {
        renderer = new Renderer();
        Vector3f CameraPosition = new Vector3f(fx, fy, fz);
        Vector3f CameraRo = new Vector3f(-90, 180, 0);
        camera = new Camera(CameraPosition, CameraRo);
        cameraInc = new Vector3f(0, 0, 0);//鐓х浉鏈洪�熷害
        dpi = 5;
        lightAngle = -90;
        flying = false;
        f_pressed = false;

        meshs = new ArrayList<>();
    }

    public void initMeshs() throws Exception {
    	/*
        float reflectance = 0.1f;
        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
        Texture texture = new Texture("/textures/grassblock.png");
        Material material = new Material(texture, reflectance);
        mesh.setMaterial(material);
        meshs.add(mesh);*/
    	Blocklist.initblocks();
    	meshs = Blocklist.getmeshes();
    	
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        // 鍔犺浇鎵�鏈夋ā鍨嬬殑 mesh
        initMeshs();

        // 鍔犺浇鍦板浘
        if (Main.Start_type == 0) {
        	Generator ge = new Generator(32, 32, "default");
        	map = new Map(ge.getItems());
        }
        
        if (Main.Start_type == 1)
        {
        	map = new Map();
			File rfile;
			BufferedReader reader = null;
			rfile = new File("record\\map_record.log");
			try {
	        	reader = new BufferedReader(new FileReader(rfile));
	        	String temp = reader.readLine();
	        	while (temp != null) {
	        		String st[] = temp.split(" ");
	        		map.addBlock(Integer.valueOf(st[0]), Integer.valueOf(st[1]), Integer.valueOf(st[2]), Integer.valueOf(st[3]));
	        		temp = reader.readLine();
	        	}
	        	reader.close();
			} catch (IOException e) {
	            e.printStackTrace();
	        }
        }

        scene = new Scene();

        for (Block block : map.getBlocks()) {
            scene.addGameItem(getGameItem(block));
        }

        // 璁剧疆 skybox
        float skyBoxScale = 50.0f;
        SkyBox skyBox = new SkyBox("/models/skybox.obj", "/textures/skybox.png");
        skyBox.setScale(skyBoxScale);
        scene.setSkyBox(skyBox);

        setupLights();

        // 鍒涘缓 hud
        hud = new Hud("DEMO");
    }

    private void setupLights() {
        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);

        // Ambient Light
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

        // Directional Light
        float lightIntensity = 1.0f;
        Vector3f lightPosition = new Vector3f(1, 0, 0);
        sceneLight.setDirectionalLight(new DirectionalLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity));
    }

    public boolean isBlockExist(Vector3f position) {
        GameItem tmpItem = new GameItem();
        position.x = Math.round(position.x);
        position.y = Math.round(position.y);
        position.z = Math.round(position.z);
        // System.out.printf("isBlockExist: %f %f %f\n", position.x, position.y, position.z);
        tmpItem.setPosition(position);
        return map.getBlock((int) position.x, (int) position.y, (int) position.z) != null;
    }

    public boolean isOnBlock(Vector3f position) {
        // System.out.printf("isOnBlock: %f %f %f\n", position.x, position.y, position.z);
        float dx[] = {0.5f, -0.5f}, dz[] = {0.5f, -0.5f};
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++) {
                Vector3f pos = new Vector3f();
                pos.x = position.x + dx[i];
                pos.z = position.z + dz[j];
                pos.y = position.y - 1f;
                if (isBlockExist(pos))
                    return true;
            }
        return false;
    }

    public boolean isLegal(Vector3f position) {
        float dx[] = {0.1f, -0.1f}, dz[] = {0.1f, -0.1f}, dy[] = {0f, 0.1f};
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    Vector3f pos = new Vector3f();
                    pos.x = position.x + dx[i];
                    pos.z = position.z + dz[j];
                    pos.y = position.y + dy[k];
                    if (isBlockExist(pos))
                        return false;
                }
            }
        return true;
    }

    public GameItem getGameItem(Block block) {
        GameItem gameItem = new GameItem();
        gameItem.setPosition(block.getX(), block.getY(), block.getZ());
        gameItem.setMesh(meshs.get(block.getId()));
        gameItem.setScale(0.5f);
        return gameItem;
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        if (window.isKeyPressed(GLFW_KEY_W)) {
            movz = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            movz = 1;
        } else if (window.isKeyPressed(GLFW_KEY_A)) {
            movx = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            movx = 1;
        }

        if (window.isKeyPressed(GLFW_KEY_G)) {
        	Save.SaveMap(camera, map);
        }
        
        if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            if (!flying && isOnBlock(camera.getPosition())) {
                cameraInc.y = jump_initial_speed;
            }
        }

        if (window.isKeyReleased(GLFW_KEY_F) && f_pressed) {
            flying = !flying;
            f_pressed = false;
        }
        if (window.isKeyPressed(GLFW_KEY_F)) {
            f_pressed = true;
        }

        if (window.isKeyPressed(GLFW_KEY_SPACE) && flying) {
            movy = 1;
        } else if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT) && flying) {
            movy = -1;
        }
        
        if (window.isKeyPressed(GLFW_KEY_Q)) {
        	if (pastkeyCode != GLFW_KEY_Q)
        		curblock = Blocklist.switchplace(curblock, -1);
        	pastkeyCode = GLFW_KEY_Q;
        }else if (window.isKeyPressed(GLFW_KEY_E)) {
        	if (pastkeyCode != GLFW_KEY_E)
        		curblock = Blocklist.switchplace(curblock, 1);
        	pastkeyCode = GLFW_KEY_E;
        }
        else {
        	pastkeyCode = -1;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        DirectionalLight directionalLight = scene.getSceneLight().getDirectionalLight();
        lightAngle += 0.1f;
        if (lightAngle > 90) {
            directionalLight.setIntensity(0);
            if (lightAngle >= 180) {
                lightAngle = -90;
            }
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (Math.abs(lightAngle) - 80) / 10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        } else {
            directionalLight.setIntensity(1);
            directionalLight.getColor().x = 1;
            directionalLight.getColor().y = 1;
            directionalLight.getColor().z = 1;
        }
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);

        boolean onBlock = isOnBlock(camera.getPosition());

        if (!flying) {
            // 澶勭悊閲嶅姏
            cameraInc.y += grav_acc * interval;
            if (onBlock && cameraInc.y < 0) {
                /*
                if (cameraInc.y < -0.5) {
                    while (isOnBlock2(camera.getPosition()))
                        camera.setPosition(camera.getPosition().x, camera.getPosition().y + 0.01f, camera.getPosition().z);
                    camera.setPosition(camera.getPosition().x, camera.getPosition().y - 0.01f, camera.getPosition().z);
                }
                 */
                cameraInc.y = 0;
            }
        } else {
            // y 杞村噺閫�
            float ny = cameraInc.y + (cameraInc.y > 0 ? frac_acc : -frac_acc) * interval;
            cameraInc.y = (ny * cameraInc.y > 0) ? ny : 0;

            // y 杞村姞閫�
            if (movy != 0) {
                cameraInc.y += movy * move_acc * interval;
                cameraInc.y = Math.min(cameraInc.y, move_max_speed);
                cameraInc.y = Math.max(cameraInc.y, -move_max_speed);
                movy = 0;
            }
        }

        // x/z 杞村噺閫�
        float nx = cameraInc.x + (onBlock ? 1f : 0.5f) * (cameraInc.x > 0 ? frac_acc : -frac_acc) * interval;
        float nz = cameraInc.z + (onBlock ? 1f : 0.5f) * (cameraInc.z > 0 ? frac_acc : -frac_acc) * interval;
        cameraInc.x = (nx * cameraInc.x > 0) ? nx : 0;
        cameraInc.z = (nz * cameraInc.z > 0) ? nz : 0;

        // x/z 杞村姞閫�
        if (movx != 0) {
            cameraInc.x += movx * move_acc * interval;
            cameraInc.x = Math.min(cameraInc.x, move_max_speed);
            cameraInc.x = Math.max(cameraInc.x, -move_max_speed);
            movx = 0;
        }
        if (movz != 0) {
            cameraInc.z += movz * move_acc * interval;
            cameraInc.z = Math.min(cameraInc.z, move_max_speed);
            cameraInc.z = Math.max(cameraInc.z, -move_max_speed);
            movz = 0;
        }

        // 绉诲姩鎽勫儚鏈�
        Vector3f tmpPosition = camera.tempMovePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        GameItem tmpCamera = new GameItem();
        tmpCamera.setPosition((int) (Math.round(tmpPosition.x)), (int) (Math.floor(tmpPosition.y)), (int) (Math.round(tmpPosition.z)));
        if (isLegal(tmpPosition)) {

            camera.movePosition(cameraInc.x * CAMERA_POS_STEP, (cameraInc.y) * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        }

        // 鏃嬭浆鎽勫儚鏈�
        Vector2f rotVec = mouseInput.getDisplVec();
        if (rotVec.x * MOUSE_SENSITIVITY * dpi + camera.getRotation().x > 89.9) //闇�瑕佽�冭檻缈昏浆杩囨潵鐨勬儏鍐�,鍗宠瑙掓棆杞笉鑳借秴杩囦竴涓寖鍥达紙淇话-90鍒�90锛�
            camera.moveRotation(0, rotVec.y * MOUSE_SENSITIVITY * dpi, 0);
        else if (rotVec.x * MOUSE_SENSITIVITY * dpi + camera.getRotation().x < -89.9)
            camera.moveRotation(0, rotVec.y * MOUSE_SENSITIVITY * dpi, 0);
        else
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY * dpi, rotVec.y * MOUSE_SENSITIVITY * dpi, 0);

        // 绉婚櫎鏂瑰潡
        if (mouseInput.isRightButtonPressed() && System.currentTimeMillis() - lasDig > 500) {
            try {
                Block block = selectBlock();
                if (map.getBlock(block) != null) {
                    map.removeBlock(block);
                    scene.removeGameItem(getGameItem(block));
                    lasDig = System.currentTimeMillis();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

        // 鏀剧疆鏂瑰潡
        if (mouseInput.isLeftButtonPressed() && System.currentTimeMillis() - lasPut > 500) {
            try {
                Block selectBlock = selectSetBlock();
                if (selectBlock != null) {
                    int new_x = selectBlock.getX();
                    int new_y = selectBlock.getY();
                    int new_z = selectBlock.getZ();

                    if (map.getBlock(new_x, new_y, new_z) == null) {
                        map.addBlock(new_x, new_y, new_z, curblock);
                        scene.addGameItem(getGameItem(new Block(new_x, new_y, new_z, curblock)));
                        lasPut = System.currentTimeMillis();
                    }
                } else {
                    System.out.print("no block\n");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.print(e.toString());
            }
        }
    }

    @Override
    public void render(Window window) {
        hud.updateSize(window);
        hud.setStatusText1(String.format("x = %.2f, y = %.2f, z = %.2f",
                camera.getPosition().x, camera.getPosition().y, camera.getPosition().z)
        );
        hud.setStatusText2(String.format("lightAngle = %.2f\n", lightAngle));
        renderer.render(window, camera, scene, hud);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        scene.cleanup();
    }

}
