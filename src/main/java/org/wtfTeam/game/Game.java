package org.wtfTeam.game;

import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

import org.wtfTeam.engine.*;
import org.wtfTeam.engine.Window;
import org.wtfTeam.engine.graph.lights.DirectionalLight;
import org.wtfTeam.engine.items.GameItem;
import org.wtfTeam.engine.graph.*;
import org.wtfTeam.engine.items.SkyBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Game implements IGameLogic {

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

    private Toolbar toolbar;

    private IHud huds[];

    private long lasPut = 0, lasDig = 0;

    private GameItem pig;

    private long lasSelGroup = 0;

    public Game() {
        renderer = new Renderer();
        Vector3f CameraPosition = new Vector3f(0f, 15f, 0f);
        Vector3f CameraRo = new Vector3f(-90, 180, 0);
        camera = new Camera(CameraPosition, CameraRo);
        cameraInc = new Vector3f(0, 0, 0);//照相机速度
        dpi = 5;
        lightAngle = -90;
        flying = false;
        f_pressed = false;

        meshs = new ArrayList<>();
    }

    public Game(float fx, float fy, float fz) {
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
        Blocklist.initblocks();
        meshs = Blocklist.getmeshes();
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        // 加载所有模型的 mesh
        initMeshs();

        if (Main.Start_type == 0) {
            Generator ge = new Generator(32, 32, "default");
            map = new Map(ge.getItems());
        }

        if (Main.Start_type == 1) {
            map = new Map();
            File rfile;
            BufferedReader reader = null;
            rfile = new File("map_record.log");
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

        // 创建 scene
        scene = new Scene();

        for (Block block : map.getBlocks()) {
            scene.addGameItem(getGameItem(block));
        }

        /*
        pig = new GameItem();
        pig.setPosition(2f, 1f, 2f);
        pig.setMesh(meshs.get(1));
        pig.setScale(0.03f);
        scene.addGameItem(pig);
*/
        // 设置 skybox
        float skyBoxScale = 50.0f;
        SkyBox skyBox = new SkyBox("/models/skybox.obj", "/textures/skybox.png");
        skyBox.setScale(skyBoxScale);
        scene.setSkyBox(skyBox);

        setupLights();

        // 创建 hud
        hud = new Hud("DEMO");
        toolbar = new Toolbar();
        huds = new IHud[]{hud, toolbar};

        toolbar.setSelGroup(0);
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

    public Block getBlock(Vector3f position) {
        GameItem tmpItem = new GameItem();
        position.x = Math.round(position.x);
        position.y = Math.round(position.y);
        position.z = Math.round(position.z);
        tmpItem.setPosition(position);
        return map.getBlock((int) position.x, (int) position.y, (int) position.z);
    }

    public boolean isOnBlock(Vector3f position) {
        float dx[] = {0.1f, -0.1f}, dz[] = {0.1f, -0.1f};
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++) {
                Vector3f pos = new Vector3f();
                pos.x = position.x + dx[i];
                pos.z = position.z + dz[j];
                pos.y = position.y - 1f;
                Block t = getBlock(pos);
                if (t != null)
                    return true;
            }
        return false;
    }

    public boolean isLegal(Vector3f position) {
        float dx[] = {0.1f, -0.1f}, dz[] = {0.1f, -0.1f}, dy[] = {-0.1f, 0.1f};
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    Vector3f pos = new Vector3f();
                    pos.x = position.x + dx[i];
                    pos.z = position.z + dz[j];
                    pos.y = position.y + dy[k];
                    Block t = getBlock(pos);
                    if (t != null && !t.getTransp())
                        return false;
                }
            }
        return true;
    }

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
            System.out.printf("%f %f %f\n", selectPosition.x, selectPosition.y, selectPosition.z);
            System.out.printf("%d %d %d\n", (int) (Math.rint(selectPosition.x)), (int) (Math.rint(selectPosition.y)), (int) (Math.rint(selectPosition.z)));
            if (block != null) {
                return block;
            }
        }
        return null;
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
            movz = -2;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            movz = 2;
        } else if (window.isKeyPressed(GLFW_KEY_A)) {
            movx = -2;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            movx = 2;
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

        if (window.isKeyPressed(GLFW_KEY_1)) {
            toolbar.setSelPos(0);
        } else if (window.isKeyPressed(GLFW_KEY_2)) {
            toolbar.setSelPos(1);
        } else if (window.isKeyPressed(GLFW_KEY_3)) {
            toolbar.setSelPos(2);
        } else if (window.isKeyPressed(GLFW_KEY_4)) {
            toolbar.setSelPos(3);
        } else if (window.isKeyPressed(GLFW_KEY_5)) {
            toolbar.setSelPos(4);
        } else if (window.isKeyPressed(GLFW_KEY_6)) {
            toolbar.setSelPos(5);
        } else if (window.isKeyPressed(GLFW_KEY_7)) {
            toolbar.setSelPos(6);
        } else if (window.isKeyPressed(GLFW_KEY_8)) {
            toolbar.setSelPos(7);
        } else if (window.isKeyPressed(GLFW_KEY_9)) {
            toolbar.setSelPos(8);
        }

        if (window.isKeyPressed(GLFW_KEY_R) && System.currentTimeMillis() - lasSelGroup > 400) {
            lasSelGroup = System.currentTimeMillis();
            int t = toolbar.getSelGroup();
            t = (t + 1) % toolbar.groupCount;
            toolbar.setSelGroup(t);
        }
    }

    private Vector3f pigVec = new Vector3f(0f, 0f, 0f);
    private long lasPigVecChange = 0;

    @Override
    public void update(float interval, MouseInput mouseInput) {
        /*
        scene.removeGameItem(pig);
        if (System.currentTimeMillis() - lasPigVecChange > 500 + 500 * Math.random()) {
            System.out.println("!");
            Vector3f t = new Vector3f((float) (Math.random() - 0.5) * 0.01f, (float) (Math.random() - 0.5) * 0.001f, (float) (Math.random() - 0.5) * 0.01f);
            pigVec.add(t);
            lasPigVecChange = System.currentTimeMillis();
        }
        pig.getPosition().add(pigVec);
        scene.addGameItem(pig);
*/
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
            // 处理重力
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
            // y 轴减速
            float ny = cameraInc.y + (cameraInc.y > 0 ? frac_acc : -frac_acc) * interval;
            cameraInc.y = (ny * cameraInc.y > 0) ? ny : 0;

            // y 轴加速
            if (movy != 0) {
                cameraInc.y += movy * move_acc * interval;
                cameraInc.y = Math.min(cameraInc.y, move_max_speed);
                cameraInc.y = Math.max(cameraInc.y, -move_max_speed);
                movy = 0;
            }
        }

        // x/z 轴减速
        float nx = cameraInc.x + (onBlock ? 1f : 0.5f) * (cameraInc.x > 0 ? frac_acc : -frac_acc) * interval;
        float nz = cameraInc.z + (onBlock ? 1f : 0.5f) * (cameraInc.z > 0 ? frac_acc : -frac_acc) * interval;
        cameraInc.x = (nx * cameraInc.x > 0) ? nx : 0;
        cameraInc.z = (nz * cameraInc.z > 0) ? nz : 0;

        // x/z 轴加速
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

        // 移动摄像机
        Vector3f tmpPosition = camera.tempMovePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        GameItem tmpCamera = new GameItem();
        tmpCamera.setPosition((int) (Math.round(tmpPosition.x)), (int) (Math.floor(tmpPosition.y)), (int) (Math.round(tmpPosition.z)));
        if (isLegal(tmpPosition)) {
            camera.movePosition(cameraInc.x * CAMERA_POS_STEP, (cameraInc.y) * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        }

        // 旋转摄像机
        Vector2f rotVec = mouseInput.getDisplVec();
        if (rotVec.x * MOUSE_SENSITIVITY * dpi + camera.getRotation().x > 89.9) //需要考虑翻转过来的情况,即视角旋转不能超过一个范围（俯仰-90到90）
            camera.moveRotation(0, rotVec.y * MOUSE_SENSITIVITY * dpi, 0);
        else if (rotVec.x * MOUSE_SENSITIVITY * dpi + camera.getRotation().x < -89.9)
            camera.moveRotation(0, rotVec.y * MOUSE_SENSITIVITY * dpi, 0);
        else
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY * dpi, rotVec.y * MOUSE_SENSITIVITY * dpi, 0);

        // 移除方块
        if (mouseInput.isRightButtonPressed() && System.currentTimeMillis() - lasDig > 500) {
            try {
                Block block = selectBlock();
                if (map.getBlock(block) != null && !Blocklist.Bdic.get(block.getId()).no_crash) {
                    map.removeBlock(block);
                    scene.removeGameItem(getGameItem(block));
                    lasDig = System.currentTimeMillis();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        // 放置方块
        if (mouseInput.isLeftButtonPressed() && System.currentTimeMillis() - lasPut > 500) {
            try {
                Block block = selectSetBlock();
                if (block != null) {
                    int new_x = block.getX();
                    int new_y = block.getY();
                    int new_z = block.getZ();

                    if (!camera.getPosition().equals(new Vector3f(new_x, new_y, new_z), 0.5f)) {
                        if (map.getBlock(new_x, new_y, new_z) == null) {
                            map.addBlock(new_x, new_y, new_z, toolbar.getSelId());
                            scene.addGameItem(getGameItem(new Block(new_x, new_y, new_z, toolbar.getSelId())));
                            lasPut = System.currentTimeMillis();
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void render(Window window) {
        hud.updateSize(window);
        hud.setStatusText1(String.format("x = %.2f, y = %.2f, z = %.2f",
                camera.getPosition().x, camera.getPosition().y, camera.getPosition().z)
        );
        hud.setStatusText2(String.format("lightAngle = %.2f", lightAngle));
        hud.setStatusText3(String.format("flying = %s\n", flying ? "true" : "false"));
        toolbar.updateSize(window);
        renderer.render(window, camera, scene, huds);
    }

    @Override
    public void save() {
        Save.SaveMap(camera, map);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        scene.cleanup();
    }

}
