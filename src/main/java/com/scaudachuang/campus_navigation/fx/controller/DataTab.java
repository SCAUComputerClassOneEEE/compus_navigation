package com.scaudachuang.campus_navigation.fx.controller;

import com.scaudachuang.campus_navigation.fx.model.DataEnum;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class DataTab<E> extends Tab {
    //泛型类
    private final Class<?> EType;
    //TableView动态数据
    private final ObservableList<E> eObservableList;
    //TableView
    private final TableView<E> tableView;

    public DataTab(DataEnum.DataForm dataForm, List<E> eList) throws ClassNotFoundException {

        //泛型数据装载
        EType = Class.forName(DataEnum.toUrl(dataForm));
        //Tab标头
        super.setText(String.valueOf(dataForm));

        //初始化TableView
        eObservableList = FXCollections.observableArrayList(eList);
        tableView = new TableView<>(eObservableList);
        super.setContent(tableView);
        initTableColumns();
    }

    private void initTableColumns(){
        //获得泛型类的数据域
        Field[] fields = EType.getDeclaredFields();
        for (Field field : fields){
            //变量名
            String columnName = field.getName();
            //表属性
            TableColumn<E,String> eTableColumn = new TableColumn<>(columnName);
            tableView.getColumns().add(eTableColumn);
            //方法名，例如：id->getId
            String methodName = "get" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
            eTableColumn.setCellValueFactory(param -> {
                try {
                    //获得泛型类的实例data
                    Object data = param.getValue();
                    //获得实例的get方法
                    Method method = param.getValue().getClass().getMethod(methodName);
                    //反射执行get方法
                    return new SimpleStringProperty(method.invoke(data).toString());
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return null;
            });
        }
    }

    /*
    对eObservableList的增删改查操作
     */
    public void addElement(){

    }
    public void deleteElement(){
        eObservableList.clear();
    }
    public void checkElement(){

    }
    public void updateElement(){

    }
}
