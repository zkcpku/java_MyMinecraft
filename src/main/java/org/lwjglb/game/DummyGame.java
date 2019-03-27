package org.lwjglb.game;

import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjglb.engine.GameItem;
import org.lwjglb.engine.IGameLogic;
import org.lwjglb.engine.MouseInput;
import org.lwjglb.engine.Window;
import org.lwjglb.engine.graph.Camera;
import org.lwjglb.engine.graph.Mesh;
import org.lwjglb.engine.graph.OBJLoader;
import org.lwjglb.engine.graph.Texture;

import java.util.ArrayList;
import java.util.List;

public class DummyGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

//    private GameItem[] gameItems;
//    private GameItem[][] gameItems2;
//    private  GameItem[][][] gameItems3;
    private List<GameItem> gameItems;

    private int dpi;

    private static final float CAMERA_POS_STEP = 0.05f;

    private static final float SELECT_SENSITIVITY = 0.5f;

    private static final float LIMIT_SELECT = 1.0f;


    private static int jump_step = 0;

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
        Vector3f CameraPosition = new Vector3f(0,1,0);
        Vector3f CameraRo = new Vector3f(-90,180,0);
        renderer = new Renderer();
        camera = new Camera(CameraPosition,CameraRo);
        cameraInc = new Vector3f(0, 0, 0);//照相机速度
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);//渲染器初始化窗口

        //Mesh mesh = OBJLoader.loadMesh("/models/bunny.obj");
        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");//加载模型，画图3D
        Texture texture = new Texture("/textures/grassblock.png");//加载图片
        mesh.setTexture(texture);//设置好图片
//        GameItem gameItem = new GameItem(mesh);//得到一个块item
//        gameItem.setScale(0.5f);//大小为0.5，所以这里的块一个占1，比如-2和-3的两个块是紧邻的
//        gameItem.setPosition(0, 0, -2);
//
//        GameItem gameItem1 = new GameItem(mesh);
//        gameItem1.setScale(0.5f);
//        gameItem1.setPosition(0,0,-3);
//        gameItems = new GameItem[]{gameItem, gameItem1};


//        gameItems = new GameItem[5];
//        for (int i = 0;i < 5; ++i)
//        {
//            gameItems[i] = new GameItem(mesh);
//            gameItems[i].setScale(0.5f);
//            gameItems[i].setPosition(0,0,i);
//        }
//          gameItems2 = new GameItem[20][20];
        gameItems = new ArrayList<GameItem>();
          for (int i = 0;i < 30; ++i) {
              for (int j = 0;j < 20; ++j){
                  GameItem gameItem = new GameItem(mesh);//注意这里mesh是一样的，导致初始化的这些方格如果mesh被cleanup则全部被cleanup
                  gameItem.setScale(0.5f);
                  gameItem.setPosition(i, 0, j);
                  gameItems.add(gameItem);
              }
          }

    }

    @Override
    public void input(Window window, MouseInput mouseInput) {

//        Vector2f rotVec = mouseInput.getDisplVec();
//        if (rotVec.x * MOUSE_SENSITIVITY * dpi + camera.getRotation().x > 90)
//            camera.moveRotation(0, rotVec.y * MOUSE_SENSITIVITY * dpi, 0);
//        else if(rotVec.x * MOUSE_SENSITIVITY * dpi + camera.getRotation().x < -90)
//            camera.moveRotation(0, rotVec.y * MOUSE_SENSITIVITY * dpi, 0);
//        else//需要考虑翻转过来的情况,即视角旋转不能超过一个范围（俯仰-90到90）
//            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY * dpi, rotVec.y * MOUSE_SENSITIVITY * dpi, 0);
        cameraInc.set(0, -1, 0);//照相机位置的修改值，每个loop都会修改，默认为0，0，0，表示不按键就不动
        if (jump_step != 0)//还在上跳过程
        {
            cameraInc.set(0,2,0);
            jump_step -= 1;
        }
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            GameItem belowPosition = new GameItem();
            belowPosition.setPosition((int)(Math.round(camera.getPosition().x)),(int)(Math.floor(camera.getPosition().y)),(int)(Math.floor(camera.getPosition().z)));
            if (gameItems.contains(belowPosition) && jump_step == 0){//如果已经落地
                cameraInc.y = 2;
                jump_step = 25;//开始起跳
            }

        }
        if (window.isKeyPressed(GLFW_KEY_B)){//可以用来打印debug信息
            System.out.printf("camera position: %f %f %f\n",camera.getPosition().x,camera.getPosition().y,camera.getPosition().z);
            System.out.printf("approximately block: %d %d %d\n",(int)(Math.round(camera.getPosition().x)),(int)(Math.round(camera.getPosition().y)),(int)(Math.floor(camera.getPosition().z)));
        }

    }

    @Override
    public void update(float interval, MouseInput mouseInput){
        // Update camera position
//        Vector3f tmpPostion = camera.tempMovePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
//        GameItem tmpItem = new GameItem();
//        tmpItem.setPosition((int)tmpPostion.x, (int)tmpPostion.y,(int)tmpPostion.z);
//        if (gameItems.contains(tmpItem))
//            cameraInc.set(0, 0, 0);

//      检查是否卡bug
        GameItem nowPosition = new GameItem();
        nowPosition.setPosition((int)(Math.round(camera.getPosition().x)),(int)(Math.round(camera.getPosition().y)),(int)(Math.floor(camera.getPosition().z)));
        if (gameItems.contains(nowPosition))
        {
            camera.movePosition(cameraInc.x * CAMERA_POS_STEP, (cameraInc.y) * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        }
        else
        {
            //      首先检查是否落地
            GameItem belowPosition = new GameItem();
            belowPosition.setPosition((int)(Math.round(camera.getPosition().x)),(int)(Math.floor(camera.getPosition().y)),(int)(Math.floor(camera.getPosition().z)));
            if (gameItems.contains(belowPosition) && cameraInc.y <= 0)//如果已经落地
                cameraInc.y = 0;


            Vector3f tmpPosition = camera.tempMovePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
            GameItem tmpCamera = new GameItem();
            tmpCamera.setPosition((int)(Math.round(tmpPosition.x)), (int)(Math.floor(tmpPosition.y + 0.5)),(int)(Math.round(tmpPosition.z)));
            if (!gameItems.contains(tmpCamera)){
                tmpCamera.setPosition((int)(Math.round(tmpPosition.x)), (int)(Math.floor(tmpPosition.y + 1)),(int)(Math.round(tmpPosition.z)));
                if(!gameItems.contains(tmpCamera))
                    camera.movePosition(cameraInc.x * CAMERA_POS_STEP, (cameraInc.y) * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
            }
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
//            Vector3f eyePositon = camera.getEyePosition(-25 * CAMERA_POS_STEP);
//            System.out.printf("%d %d %d\n",(int)eyePositon.x,(int)eyePositon.y ,(int)eyePositon.z);

//            Vector3f eyePositon = (camera.getPosition());
//            eyePositon.add(camera.getFront().mul(CAMERA_POS_STEP ));

//            System.out.printf("%f %f %f| %f %f %f\n",camera.getFront().x,camera.getFront().y,camera.getFront().z,camera.getPosition().x,camera.getPosition().y,camera.getPosition().z);
            try {
                GameItem selectBlock = selectSetBlock();
                if (selectBlock != null){
                    int new_x = (int)(selectBlock.getPosition().x);
                    int new_y = (int)(selectBlock.getPosition().y);
                    int new_z = (int)(selectBlock.getPosition().z);
                    Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");//加载模型，画图3D
                    Texture texture = new Texture("/textures/grassblock.png");//加载图片
                    mesh.setTexture(texture);//设置好图片
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
        renderer.render(window, camera, gameItems);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
            for (GameItem gameItem : gameItems) {
                if (gameItem == null)   continue;
                gameItem.getMesh().cleanUp();
            }
    }

}
