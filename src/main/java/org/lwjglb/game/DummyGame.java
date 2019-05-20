package org.lwjglb.game;

import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjglb.engine.GameItem;
import org.lwjglb.engine.IGameLogic;
import org.lwjglb.engine.Map;
import org.lwjglb.engine.MouseInput;
import org.lwjglb.engine.Window;
import org.lwjglb.engine.graph.*;

import java.util.ArrayList;
import java.util.List;

public class DummyGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private Vector3f ambientLight;

    private DirectionalLight directionalLight;

    private Vector3f dirLightCenter;

//    private GameItem[] gameItems;
//    private GameItem[][] gameItems2;
//    private  GameItem[][][] gameItems3;
    private List<GameItem> gameItems;

    private int dpi;

    private static final float CAMERA_POS_STEP = 0.05f;

    private static final float SELECT_SENSITIVITY = 0.5f;

    private static final float LIMIT_SELECT = 1.0f;

    private static final float jump_initial_speed = 3.0f;
    private static final float jump_max_speed = 20f;
    private static final float move_max_speed = 1.2f;
    private static final float move_acc = 10f;
    private static final float grav_acc = -9.0f;
    private static final float frac_acc = -5.0f;

    private static int movx, movz;

    private float lightAngle;

    private Hud hud;

    public final GameItem selectSetBlock() {
        Vector3f selectPosition = new Vector3f(camera.getPosition());

        GameItem rst = new GameItem();
        for (float dist = SELECT_SENSITIVITY * 2; dist <= LIMIT_SELECT ; dist+=SELECT_SENSITIVITY * 2) {
            selectPosition.add(camera.getFront().mul(SELECT_SENSITIVITY * 2));
            rst.setPosition((int)(Math.rint(selectPosition.x)),(int)(Math.rint(selectPosition.y)),(int)(Math.rint(selectPosition.z)));
           if (!gameItems.contains(rst)){
               System.out.printf(" %f : %f %f %f\n",dist,((selectPosition.x)),((selectPosition.y)),((selectPosition.z)));
               System.out.printf(" %f : %d %d %d\n",dist,(int)(Math.rint(selectPosition.x)),(int)(Math.rint(selectPosition.y)),(int)(Math.rint(selectPosition.z)));

//               if(camera.getPosition().x > Math.rint(selectPosition.x))
//                    rst.setPosition((int)(Math.rint(selectPosition.x) + 1),(int)(Math.rint(selectPosition.y)),(int)(Math.rint(selectPosition.z)));
//                else if(camera.getPosition().x < Math.rint(selectPosition.x))
//                    rst.setPosition((int)(Math.rint(selectPosition.x) - 1),(int)(Math.rint(selectPosition.y)),(int)(Math.rint(selectPosition.z)));
//                if(camera.getPosition().y > Math.rint(selectPosition.y))
//                    rst.setPosition((int)(Math.rint(selectPosition.x)),(int)(Math.rint(selectPosition.y) + 1),(int)(Math.rint(selectPosition.z)));
//                else if(camera.getPosition().y < Math.rint(selectPosition.y))
//                    rst.setPosition((int)(Math.rint(selectPosition.x)),(int)(Math.rint(selectPosition.y) - 1),(int)(Math.rint(selectPosition.z)));
//                if(camera.getPosition().z > Math.rint(selectPosition.z))
//                    rst.setPosition((int)(Math.rint(selectPosition.x)),(int)(Math.rint(selectPosition.y)),(int)(Math.rint(selectPosition.z) + 1));
//                else if(camera.getPosition().z < Math.rint(selectPosition.z))
//                    rst.setPosition((int)(Math.rint(selectPosition.x)),(int)(Math.rint(selectPosition.y)),(int)(Math.rint(selectPosition.z) - 1));
//               System.out.printf(" %f : %d %d %d\n",dist,(int)(rst.getPosition().x),(int)(rst.getPosition().y),(int)(rst.getPosition().z));

               return rst;
            }
        }
        return null;
    }

    public final GameItem selectBlock(){
        Vector3f selectPosition = new Vector3f(camera.getPosition());

        GameItem rst = new GameItem();
        for (float dist = SELECT_SENSITIVITY; dist <= LIMIT_SELECT ; dist+=SELECT_SENSITIVITY) {
            selectPosition.add(camera.getFront().mul(SELECT_SENSITIVITY));
            rst.setPosition((int)(Math.rint(selectPosition.x)),(int)(Math.rint(selectPosition.y)),(int)(Math.rint(selectPosition.z)));
//            System.out.printf(" %f : %f %f %f\n",dist,((selectPosition.x)),((selectPosition.y)),((selectPosition.z)));
//            System.out.printf(" %f : %d %d %d\n",dist,(int)((selectPosition.x)),(int)((selectPosition.y)),(int)((selectPosition.z)));
            if (gameItems.contains(rst)){

                return gameItems.get(gameItems.indexOf(rst));
            }
        }
        return null;
    }

    public DummyGame() {
        dpi = 5;
        Vector3f CameraPosition = new Vector3f(0f,15f,0f);
        Vector3f CameraRo = new Vector3f(-90,180,0);
        renderer = new Renderer();
        camera = new Camera(CameraPosition, CameraRo);
        cameraInc = new Vector3f(0, 0, 0);//照相机速度
        lightAngle = -90;
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);//渲染器初始化窗口

        float reflectance = 0.1f;
        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
        Texture texture = new Texture("/textures/grassblock.png");
        Material material = new Material(texture, reflectance);
        mesh.setMaterial(material);

        gameItems = new ArrayList<GameItem>();
        Map debugMap = new Map(32, 32);
        int bias = 15;
        for (int i = -15; i < 16; ++i) {
            for (int j = -15; j < 16; ++j)
            	for (int k = 0; k < debugMap.getHeight(i + bias, j + bias); ++k){
                GameItem gameItem = new GameItem(mesh); //注意这里mesh是一样的，导致初始化的这些方格如果mesh被cleanup则全部被cleanup
                gameItem.setScale(0.5f);
                gameItem.setPosition(i, k, j);
                gameItems.add(gameItem);
            }
        }

        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
        Vector3f lightColour = new Vector3f(1, 1, 1);
        Vector3f lightDirection = new Vector3f(1, 0, 0);
        float lightIntensity = 1f;
        directionalLight = new DirectionalLight(lightColour, lightDirection, lightIntensity);

        hud = new Hud("DEMO");
    }

    public boolean isBlockExist(Vector3f position) {
        GameItem tmpItem = new GameItem();
        position.x = Math.round(position.x);
        position.y = Math.round(position.y);
        position.z = Math.round(position.z);
        // System.out.printf("isBlockExist: %f %f %f\n", position.x, position.y, position.z);
        tmpItem.setPosition(position);
        return gameItems.contains(tmpItem);
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

    @Override
    public void input(Window window, MouseInput mouseInput) {
        if (window.isKeyPressed(GLFW_KEY_W)) {
            movz = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            movz = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            movx = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            movx = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_SPACE) && isOnBlock(camera.getPosition())) {
            System.out.println("JUMP");
            cameraInc.y = jump_initial_speed;
        }
        if (window.isKeyPressed(GLFW_KEY_B)){//可以用来打印debug信息
            System.out.printf("camera position: %f %f %f\n",camera.getPosition().x,camera.getPosition().y,camera.getPosition().z);
            System.out.printf("approximately block: %d %d %d\n",(int)(Math.round(camera.getPosition().x)),(int)(Math.round(camera.getPosition().y)),(int)(Math.floor(camera.getPosition().z)));


            //按b键测试切换背包
            if(hud.getTestGraphItem() == "012345678")
                hud.setTestGraphItem("876543210");
            else
                hud.setTestGraphItem("012345678");
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput){
        lightAngle += 0.1f;
        if (lightAngle > 90) {
            directionalLight.setIntensity(0);
            if (lightAngle >= 180) {
                lightAngle = -90;
            }
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (float) (Math.abs(lightAngle) - 80) / 10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        } else {
            directionalLight.setIntensity(1);
            directionalLight.getColor().x = 1;
            directionalLight.getColor().y = 1;
            directionalLight.getColor().z = 1;
        }
        System.out.printf("lightAngle = %.2f\n", lightAngle);
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);

        // 处理重力
        cameraInc.y += grav_acc * interval;
        if (isOnBlock(camera.getPosition())) {
            if (cameraInc.y < 0)
                cameraInc.y = 0;
            float nx = cameraInc.x + (cameraInc.x > 0? frac_acc: -frac_acc) * interval;
            float nz = cameraInc.z + (cameraInc.z > 0? frac_acc: -frac_acc) * interval;
            cameraInc.x = (nx * cameraInc.x > 0)? nx: 0;
            cameraInc.z = (nz * cameraInc.z > 0)? nz: 0;
        }
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


        System.out.printf("velocity = %.2f %.2f %.2f\n", cameraInc.x, cameraInc.y, cameraInc.z);

        Vector3f tmpPosition = camera.tempMovePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        GameItem tmpCamera = new GameItem();
        tmpCamera.setPosition((int)(Math.round(tmpPosition.x)), (int)(Math.floor(tmpPosition.y + 0.5)),(int)(Math.round(tmpPosition.z)));
        if (!gameItems.contains(tmpCamera)){
            tmpCamera.setPosition((int)(Math.round(tmpPosition.x)), (int)(Math.floor(tmpPosition.y + 1)),(int)(Math.round(tmpPosition.z)));
            if(!gameItems.contains(tmpCamera))
                camera.movePosition(cameraInc.x * CAMERA_POS_STEP, (cameraInc.y) * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        }



        // Update camera based on mouse
            Vector2f rotVec = mouseInput.getDisplVec();
            if (rotVec.x * MOUSE_SENSITIVITY * dpi + camera.getRotation().x > 89.9)//需要考虑翻转过来的情况,即视角旋转不能超过一个范围（俯仰-90到90）
                camera.moveRotation(0, rotVec.y * MOUSE_SENSITIVITY * dpi, 0);
            else if(rotVec.x * MOUSE_SENSITIVITY * dpi + camera.getRotation().x < -89.9)
                camera.moveRotation(0, rotVec.y * MOUSE_SENSITIVITY * dpi, 0);
            else
                camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY * dpi, rotVec.y * MOUSE_SENSITIVITY * dpi, 0);
        if (mouseInput.isRightButtonPressed())
        {
//            System.out.printf("%f %f %f\n",camera.getRotation().x,camera.getRotation().y , camera.getRotation().z);
//            System.out.printf("%d %d %d\n",(int)eyePositon.x,(int)eyePositon.y ,(int)eyePositon.z);
//
//            Vector3f eyePositon =  new Vector3f(camera.getPosition());
//            eyePositon.add(camera.getFront().mul(CAMERA_POS_STEP * 30));

            try {
                GameItem gameItem = selectBlock();
                if(gameItem != null){
                    int i = gameItems.indexOf(gameItem);
                    System.out.println(i);
//                    gameItems.get(i).getMesh().cleanUp();//不能对其进行getMesh操作的cleanup操作
                    gameItems.remove(i);
                }
            }
            catch(Exception e) {
                System.out.println(e.getMessage());
            }

        }
        if (mouseInput.isLeftButtonPressed())
        {
            try {
                GameItem selectBlock = selectSetBlock();
                if (selectBlock != null){
                    int new_x = (int)(selectBlock.getPosition().x);
                    int new_y = (int)(selectBlock.getPosition().y);
                    int new_z = (int)(selectBlock.getPosition().z);

                    float reflectance = 1f;
                    Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
                    Texture texture = new Texture("/textures/grassblock.png");
                    Material material = new Material(texture, reflectance);
                    mesh.setMaterial(material);

                    GameItem new_gameItem = new GameItem(mesh);
                    new_gameItem.setScale(0.5f);
                    new_gameItem.setPosition(new_x, new_y, new_z);
                    if(!gameItems.contains(new_gameItem)){
                        gameItems.add(new_gameItem);
                    }
                    else{
                        new_gameItem.getMesh().cleanUp();
                    }
                }
                else{
                    System.out.print("no block\n");
                }
            }
            catch(Exception e) {
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
        renderer.render(window, camera, gameItems, ambientLight, directionalLight, hud);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
            for (GameItem gameItem : gameItems) {
                if (gameItem == null) continue;
                gameItem.getMesh().cleanUp();
            }
    }

}
