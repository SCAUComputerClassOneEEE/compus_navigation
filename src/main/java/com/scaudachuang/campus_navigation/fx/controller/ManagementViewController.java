package com.scaudachuang.campus_navigation.fx.controller;

import com.scaudachuang.campus_navigation.entity.Admin;
import com.scaudachuang.campus_navigation.entity.Building;
import com.scaudachuang.campus_navigation.entity.Comment;
import com.scaudachuang.campus_navigation.entity.User;
import com.scaudachuang.campus_navigation.fx.model.DataEnum;
import com.scaudachuang.campus_navigation.service.*;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@FXMLController
@Component
public class ManagementViewController implements Initializable {

    @Resource
    private BuildingService buildingService;
    @Resource
    private AdminService adminService;
    @Resource
    private CommentService commentService;
    @Resource
    private UserService userService;
    @Resource
    private NoticeService noticeService;

    @FXML
    private TabPane tabPane;
    @FXML
    private TabPane tabPane2;
    @FXML
    private VBox vBox1;
    @FXML
    private VBox vBox2;
    @FXML
    private VBox vBox3;
    @FXML
    private Pane pane;

    @FXML
    private AnchorPane anchorPane;

    /**
     * 或者用监听器监听Map的改变
     */
    private final Map<DataEnum.DataForm, DataTab<?>> tabMap = new HashMap<>();
    private boolean reportedCommentLoad = false;
    private DataTab<?> dataTab;
    private TextArea textArea;
    private String notice;
    private Button edit;
    private Button sent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
        //选项卡可关闭
        tabPane2.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
        vBox1.setPadding(new Insets(0));
        vBox2.setPadding(new Insets(0));
        vBox3.setPadding(new Insets(0));
        textArea = new TextArea();
    }

    @FXML
    private void Admin() throws ClassNotFoundException { displayTab(DataEnum.DataForm.Admin); }
    @FXML
    private void Building() throws ClassNotFoundException { displayTab(DataEnum.DataForm.Building); }
    @FXML
    private void Comment() throws ClassNotFoundException { displayTab(DataEnum.DataForm.Comment); }
    @FXML
    private void User() throws ClassNotFoundException { displayTab(DataEnum.DataForm.User); }
    @FXML
    private void Announcement() { displayNotice(); }
    @FXML
    private void ReportedComments() throws ClassNotFoundException { disPlayReportedComment();}

    private void edit(){//点击编辑按钮，将textArea设置成可编辑状态
        textArea.setEditable(true);
    }

    private void sent() {//点击发布，保存文本内容，并将textArea设置为不可编辑状态
        textArea.setEditable(false);
        notice = textArea.getText();
        System.out.println(notice);
        saveNewNotice(notice);
//        Date date = new Date();
//        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String textContent = sf.format(date)+"\n"+notice+"\n";
//        System.out.println(textContent);
//
//        File file = new File(getClass().getResource("/static/notice").toString().substring(6));
//        FileWriter fileWriter = new FileWriter(file,true);
//        fileWriter.write(textContent);
//        fileWriter.flush();
//        fileWriter.close();
    }

    private void paneClosing(){
        if(pane.isVisible()){
            pane.getChildren().clear();
            pane.setVisible(false);
        }
        if(tabPane.isVisible()){
            tabPane.setVisible(false);
        }
        if (tabPane2.isVisible()){
            tabPane2.setVisible(false);
        }
    }

    private void displayTab(DataEnum.DataForm dataForm) throws ClassNotFoundException {
        paneClosing();
        tabPane.setVisible(true);
        if (tabMap.containsKey(dataForm)){
            tabPane.getSelectionModel().select(tabMap.get(dataForm));
        }else {
            switch (dataForm){
                case Admin:{dataTab = new DataTab<>(dataForm,adminService.findAll());break;}
                case Building:{dataTab = new DataTab<>(dataForm,buildingService.finAll());break;}
                case Comment:{dataTab = new DataTab<>(dataForm,commentService.findAll());break;}
                case User:{dataTab = new DataTab<>(dataForm,userService.findAll());break;}
                default:{

                }
            }
            tabPane.getTabs().add(dataTab);
            tabPane.getSelectionModel().select(dataTab);
            tabMap.put(dataForm,dataTab);
            dataTab.setOnClosed(event -> tabMap.remove(dataForm));
        }
    }

    private void displayNotice(){
        paneClosing();
        pane.setVisible(true);
        edit = new Button("编辑");
        sent = new Button("发布");
        sent.setLayoutY(500);
        sent.setLayoutX(700);
        sent.setPrefWidth(81);
        sent.setPrefHeight(30);
        sent.setOnAction(event -> {
            sent();
        });

        edit.setText("编辑");
        edit.setLayoutY(500);
        edit.setLayoutX(550);
        edit.setPrefWidth(81);
        edit.setPrefHeight(30);
        edit.setOnAction(event -> edit());


        textArea.setPrefHeight(300);
        textArea.setPrefWidth(600);
        textArea.setLayoutX(100);
        textArea.setLayoutY(100);
        textArea.setEditable(false);
        edit.setVisible(true);
        sent.setVisible(true);
        pane.getChildren().addAll(textArea,edit,sent);
        if(textArea.getText().isEmpty()) {
            textArea.appendText("这里发布我们的公告");
        }
    }

    public void deletedTable(Object deleted, Class<?> eType){
        switch (eType.getSimpleName()){
            case "Admin":{
                adminService.deleteAdminById(((Admin) deleted).getId());
                break;
            }
            case "Comment":{
                System.out.println(commentService);
                commentService.deleteCommentById(((Comment)deleted).getId());
                break;
            }
            case "User":{
                userService.deleteUserById(((User)deleted).getId());
                break;
            }
            case "Building":{
                buildingService.deleteBuildingById(((Building)deleted).getId());
                break;
            }
            default:{

            }
        }
    }

    private void disPlayReportedComment() throws ClassNotFoundException {
        paneClosing();
        tabPane2.setVisible(true);
        if (!reportedCommentLoad){
            reportedCommentLoad = true;
            dataTab = new DataTab<>(DataEnum.DataForm.Comment,commentService.reportedComments());
            dataTab.setText("ReportedComment");
            tabPane2.getTabs().add(dataTab);
        }
        tabPane2.getSelectionModel().select(dataTab);
        dataTab.setOnClosed(event -> reportedCommentLoad = false);
    }

    /**
     *
     * @param op 操作对象
     * @param eType 操作类型
     */
    public Object updateOrAddTable(Object op, Class<?> eType){
        switch (eType.getSimpleName()){
            case "Admin":{
                Admin admin = (Admin) op;
                if (adminService.findAdminById(admin.getId()) != null) {
                    adminService.deleteAdminById(admin.getId());
                }
                adminService.addAdmin(admin);
                return admin;
            }
            case "Comment":{
                System.out.println("failed");
                break;
            }
            case "User":{
                User user = (User) op;
                userService.addUser(user);
                return user;
            }
            case "Building":{
                Building building = (Building) op;
                if (buildingService.getBuildingById(building.getId()) != null) {
                    buildingService.deleteBuildingById(building.getId());
                }
                buildingService.addBuilding(building);
                return building;
            }
            default:{

            }
        }
        return null;
    }

    private void saveNewNotice(String string){
        noticeService.addNotice(string);
    }
}
