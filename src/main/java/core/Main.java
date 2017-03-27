package core;

import cn.edu.hit.gpcs.commons.models.Point;
import cn.edu.hit.gpcs.graphics.Figure;
import cn.edu.hit.gpcs.graphics.drawables.Axis;
import cn.edu.hit.gpcs.graphics.drawables.PointDrawable;
import cn.edu.hit.gpcs.graphics.drawables.PolygonDrawable;
import cn.edu.hit.gpcs.graphics.drawables.PolylineDrawable;
import cn.edu.hit.gpcs.server.utils.ConsoleUtil;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY;
import connection.FeatureRecord;
import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;
import model.Coordinate;
import model.Index;
import model.SerialPoint;
import model.Unit;
import model.TimeUnit;
import org.joda.time.YearMonth;
import util.DrawUtils;
import util.GeoUtils;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 判断目标设备当天耕作和某台设备的历史重耕情况
 */
public class Main extends Application {
    private static double maxDistance = 999999999;
    private static double mWorkWidth = 2.0;
    public static void main(String args[]){
        launch(args);//查重并矢量绘图
        /*
        //引导程序，找到中心点最近的两台设备
        getNearestDevices("D:/JavaProjects/BootStrapHistoryCalculator/centers0211.txt");
        //导入分类信息,归一化
        String dateString = "2017-02-13";//日期无变化
        List<String> unitLists = importFile(new File("D:/MachineLearning/SVM/general_messageTri" + dateString + ".txt"));
        ArrayList<Unit> units = getUnits(unitLists);
        Unit unitMax = getMaxUnit(units);
        ArrayList<Unit> unitsUniform = getUnitsUniform(units, unitMax);
        Unit[] devices = sortFromMinToMax(unitsUniform);
        System.out.println("边界分数为:" + devices[0].getScore() + ","
                + devices[unitsUniform.size() / 3].getScore() + ","
                + devices[(unitsUniform.size() / 3) * 2].getScore() + ","
                +devices[unitsUniform.size() - 1].getScore());
        double label1 = devices[unitsUniform.size() / 3].getScore();
        double label2 = devices[(unitsUniform.size() / 3) * 2].getScore();
        for (int i = 0;i < unitsUniform.size();i++){
            if (unitsUniform.get(i).getScore() < label1){
                unitsUniform.get(i).setLabel(1);
            }else if (unitsUniform.get(i).getScore() < label2){
                unitsUniform.get(i).setLabel(2);
            }else {
                unitsUniform.get(i).setLabel(3);
            }
        }
        //python特征信息格式
        exportPythonMessage(unitsUniform, units);
        //libSVM特征信息格式
        exportLibSVM(unitsUniform, units);
        */
    }

    /**
     * 找到中心点最近的两台设备并打印
     * @param centerFileName 中心点文件
     */
    public static void getNearestDevices(String centerFileName){
        List<ArrayList<Coordinate>> centerDevices = new ArrayList<>();//存储不同设备的中心点
        List<String> centerLists = importFile(new File(centerFileName));
        for (int i = 0;i < centerLists.size();i++){
            String centerMsgs[] = centerLists.get(i).split(" ");
            String centerStr[] = centerMsgs[2].split("-");
            ArrayList<Coordinate> centerSingleDevice = new ArrayList<>();
            for (int j = 0;j < centerStr.length;j++){
                String centerXY[] = centerStr[j].split(",");
                double centerX = Double.parseDouble(centerXY[0]);
                double centerY = Double.parseDouble(centerXY[1]);
                Coordinate center = new Coordinate(centerX, centerY);
                centerSingleDevice.add(center);
            }
            centerDevices.add(centerSingleDevice);
        }
        double centersMinDistance = maxDistance;
        Coordinate index = new Coordinate();//存储索引设备
        for (int i = 0;i < centerDevices.size() - 1;i++){
            for (int j = i + 1;j < centerDevices.size();j++){
                for (int k = 0;k < centerDevices.get(i).size();k++){
                    for (int w = 0;w < centerDevices.get(j).size();w++){
                        double distance = GeoUtils.euclideanDistance(centerDevices.get(i).get(k), centerDevices.get(j).get(w));
                        if (distance < centersMinDistance){
                            centersMinDistance = distance;
                            index.setX(i);
                            index.setY(j);
                        }
                    }
                }
            }
        }
        String device1[] = centerLists.get((int)index.getX()).split(" ");
        String device2[] = centerLists.get((int)index.getY()).split(" ");
        System.out.println("可能存在历史重耕的设备为" + device1[0] + device1[1] + "和" + device2[0] + device2[1]);
        System.out.println("最近距离为" + centersMinDistance);
    }

    /**
     * 获取特征单元
     * @param unitLists 特征字符数组
     * @return 特征单元数组
     */
    public static ArrayList<Unit> getUnits(List<String> unitLists){
        ArrayList<Unit> units = new ArrayList<>();
        for (int i = 0;i < unitLists.size();i++){
            String unitStrings[] = unitLists.get(i).split(",");
            Unit unit = new Unit(unitStrings[0],
                    Double.parseDouble(unitStrings[1]),
                    Double.parseDouble(unitStrings[2]),
                    Double.parseDouble(unitStrings[3]),
                    Double.parseDouble(unitStrings[4]),
                    Integer.parseInt(unitStrings[5]),
                    Double.parseDouble(unitStrings[6]),
                    Double.parseDouble(unitStrings[7]),
                    Double.parseDouble(unitStrings[8])
            );
            units.add(unit);
        }
        return units;
    }

    /**
     * 获取最大特征单元
     * @param units 特征单元数组
     * @return 最大特征单元
     */
    public static Unit getMaxUnit(ArrayList<Unit> units){
        Unit unitMax = new Unit("0",0,0,0,0,0,0,0,0);
        for (int i = 0;i < units.size();i++){
            if (units.get(i).getRepeatedRate() > unitMax.getRepeatedRate()){
                unitMax.setRepeatedRate(units.get(i).getRepeatedRate());
            }
            if (units.get(i).getUnqualifiedRate() > unitMax.getUnqualifiedRate()){
                unitMax.setUnqualifiedRate(units.get(i).getUnqualifiedRate());
            }
            if (units.get(i).getUnitMileage() > unitMax.getUnitMileage()){
                unitMax.setUnitMileage(units.get(i).getUnitMileage());
            }
            if (units.get(i).getBlockSize() > unitMax.getBlockSize()){
                unitMax.setBlockSize(units.get(i).getBlockSize());
            }
            if (units.get(i).getCenterSpacing() > unitMax.getCenterSpacing()){
                unitMax.setCenterSpacing(units.get(i).getCenterSpacing());
            }
            if (units.get(i).getGraphicDegree() > unitMax.getGraphicDegree()){
                unitMax.setGraphicDegree(units.get(i).getGraphicDegree());
            }
            if (units.get(i).getLeakageRate() > unitMax.getLeakageRate()){
                unitMax.setLeakageRate(units.get(i).getLeakageRate());
            }
        }
        return unitMax;
    }

    /**
     * 获取归一化特征单元
     * @param units 特征单元数组
     * @param unitMax 最大特征单元
     * @return 归一化特征单元数组
     */
    public static ArrayList<Unit> getUnitsUniform(ArrayList<Unit> units, Unit unitMax){
        ArrayList<Unit> unitsUniform = new ArrayList<>();
        for (int i = 0;i < units.size();i++){
            Unit unitNew = new Unit(units.get(i).getDeviceID(),
                    units.get(i).getGridArea(),
                    units.get(i).getRepeatedRate() / unitMax.getRepeatedRate(),
                    units.get(i).getUnqualifiedRate() / unitMax.getUnqualifiedRate(),
                    units.get(i).getUnitMileage() / unitMax.getUnitMileage(),
                    units.get(i).getBlockSize() / unitMax.getBlockSize(),
                    units.get(i).getCenterSpacing() / unitMax.getCenterSpacing(),
                    units.get(i).getGraphicDegree() / unitMax.getGraphicDegree(),
                    units.get(i).getLeakageRate() / unitMax.getLeakageRate());
            double score = (unitNew.getRepeatedRate()
                    + unitNew.getUnqualifiedRate()
                    + unitNew.getUnitMileage()
                    + unitNew.getBlockSize()
                    + unitNew.getCenterSpacing()
                    + unitNew.getGraphicDegree()
                    + unitNew.getLeakageRate()) * 10;
            unitNew.setScore(score);
            unitsUniform.add(unitNew);
        }
        return unitsUniform;
    }

    /**
     * 归一化特征单元从小到大排序
     * @param unitsUniform 归一化特征单元数组
     * @return 排序后的特征数组
     */
    public static Unit[] sortFromMinToMax(ArrayList<Unit> unitsUniform){
        Unit[] devices = new Unit[unitsUniform.size()];
        for (int i = 0;i < unitsUniform.size();i++){
            devices[i] = unitsUniform.get(i);
        }
        for (int i = 0;i < unitsUniform.size() - 1;i++){
            for (int j = i + 1;j < unitsUniform.size();j++){
                if (devices[i].getScore() > devices[j].getScore()){
                    Unit temp = devices[i];
                    devices[i] = devices[j];
                    devices[j] = temp;
                }
            }
        }
        return devices;
    }

    /**
     * 获取日期字符
     * @return 日期字符
     */
    public static String getDateString(){
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(currentTime);
    }

    /**
     * 导出libSVM特征信息:标签: 特征标号:特征值 ...
     * @param unitsUniform
     * @param units
     */
    public static void exportLibSVM(ArrayList<Unit> unitsUniform, ArrayList<Unit> units){
        String message = "";
        for (int i = 0;i < unitsUniform.size();i++){
            message += unitsUniform.get(i).getLabel() + " "
                    + "1:" + units.get(i).getGridArea() + " "
                    + "2:" + units.get(i).getRepeatedRate() + " "
                    + "3:" + units.get(i).getUnqualifiedRate() + " "
                    + "4:" + units.get(i).getUnitMileage() + " "
                    + "5:" + units.get(i).getBlockSize() + " "
                    + "6:" + units.get(i).getCenterSpacing() + " "
                    + "7:" + units.get(i).getGraphicDegree() + " "
                    + "8:" + units.get(i).getLeakageRate() + " " + "\r\n";
        }
        writeOS("D:/MachineLearning/SVM/m0217" + ".txt", message);
    }

    /**
     * 导出Python特征信息:特征值,...
     * @param unitsUniform
     * @param units
     */
    public static void exportPythonMessage(ArrayList<Unit> unitsUniform, ArrayList<Unit> units){
        String message = "";
        for (int i = 0;i < unitsUniform.size();i++){
            message += units.get(i).getDeviceID() + ","
                    + units.get(i).getGridArea() + ","
                    + units.get(i).getRepeatedRate() + ","
                    + units.get(i).getUnqualifiedRate() + ","
                    + units.get(i).getUnitMileage() + ","
                    + units.get(i).getBlockSize() + ","
                    + units.get(i).getCenterSpacing() + ","
                    + units.get(i).getGraphicDegree() + ","
                    + units.get(i).getLeakageRate() + ","
                    + unitsUniform.get(i).getLabel() + "\r\n";
        }
        writeOS("D:/MachineLearning/SVM/messageWithLabel0222" + ".txt", message);
    }

    /**
     * 使用文件流的形式导出综合信息
     * @param fileName 文件名
     * @param message 写入信息
     */
    public static void writeOS(String fileName,String message){
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName, true)));
            out.write(message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void drawRepeatedBlockOriginally(ArrayList<Coordinate> coordinates) {
        ArrayList<ArrayList<Coordinate>> edgeNew = new ArrayList<>();
        draw(coordinates, edgeNew, "重耕地块");
    }

    /**
     * 原始绘制不同地块、边界点、中心点
     * @param border 地块信息坐标
     * @param eageNew 边界信息坐标
     * @param name 标题，包括设备号和日期
     */
    public static void draw(ArrayList<Coordinate> border,ArrayList<ArrayList<Coordinate>> eageNew,String name){
        DrawUtils drawUtils = new DrawUtils(border,eageNew,name);
        drawUtils.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing( WindowEvent event )
                    {System.exit( 0 );}
                }
        );
    }

    /**
     * 地块详细信息
     * @param coordinates 标记坐标
     * @return 按照地块划分的坐标数组
     */
    public static ArrayList<ArrayList<Coordinate>> getBlockArray(ArrayList<Coordinate> coordinates){
        ArrayList<ArrayList<Coordinate>> blockArray = new ArrayList<>();
        ArrayList<Coordinate> blockNum = GeoUtils.getDiffBlockCoor(coordinates);
        for (int i = 0;i<blockNum.size();i++){
            ArrayList<Coordinate> single = new ArrayList<>();
            for (int j = 0;j<coordinates.size();j++){
                if (coordinates.get(j).getLandBlock()==blockNum.get(i).getLandBlock()){
                    single.add(coordinates.get(j));
                }
            }
            blockArray.add(single);
        }
        return blockArray;
    }

    /**
     * 计算地块面积
     * @param coordinates XY坐标数组
     * @return  网格面积
     */
    public static double calcBlockArea(ArrayList<Coordinate> coordinates) {
        double[] azimuth = findBorder(coordinates);
        List<Integer> cellDepth = countInsideArea(azimuth[0],azimuth[1],azimuth[2],azimuth[3],coordinates);//方格个数
        return mWorkWidth*mWorkWidth*cellDepth.size();
    }

    /**
     * 计算具有深度的网格个数
     * @param east 横向最大边界
     * @param west 横向最小边界
     * @param south 纵向最小边界
     * @param north 纵向最大边界
     * @param coordinates 一组XY坐标
     * @return 深度数组
     */
    public static List<Integer> countInsideArea(double east, double west, double south, double north, List<Coordinate> coordinates) {
        int flag;
        Coordinate topLeft = new Coordinate(west, north); // 每个正方形的左上角的横纵坐标
        int totalDepth;
        int depthCount;
        List<Integer> cellDepth = new ArrayList<>();
        // 按行扫描矩形区域所有单元格
        while(topLeft.getX() <= east &&
                topLeft.getX() >= west &&
                topLeft.getY() <= north &&
                topLeft.getY() >= south) {
            flag = 0;
            totalDepth = 0;//记录单元格内总深度
            depthCount = 0;//记录单元格内点个数
            // 扫描所有点，判断是否在小格中
            for (int i = 0; i < coordinates.size(); i++) {
                if(isIn(topLeft, mWorkWidth, coordinates.get(i))) {
                    flag = 1;
                    totalDepth = totalDepth + coordinates.get(i).getDepth();
                    depthCount++;
                }
            }
            if(flag == 1) { // 单元格中有点
                totalDepth = totalDepth / depthCount;
                cellDepth.add(totalDepth);
            }
            if(topLeft.getX() + mWorkWidth < east) {
                topLeft.setX(topLeft.getX() + mWorkWidth); // 在同一行下次扫面的小格左上角坐标
            } else {
                topLeft.setX(west); // 换行
                topLeft.setY(topLeft.getY() - mWorkWidth);
            }
        }
        return cellDepth;
    }

    /**
     * 判断点是否在指定正方形内
     * @param edge 边界基准
     * @param arc 边长
     * @param point 待检测坐标
     */
    public static boolean isIn(Coordinate edge, double arc, Coordinate point) {
        return point.getX() >= edge.getX() &&
                point.getX() <= edge.getX() + arc &&
                point.getY() <= edge.getY() &&
                point.getY() >= edge.getY() - arc;
    }

    /**
     * 判断点是否在多边形内部
     * @param coordinate 待判断的点
     * @param edges 边界点
     * @return 是否
     */
    public static boolean isInsidePolygon(Coordinate coordinate, ArrayList<Coordinate> edges) {
        double tolerant = 5;//Y方向容差
        boolean judge = false;
        boolean lessJudge = false;
        boolean moreJudge = false;
        ArrayList<Coordinate> sameY = new ArrayList<Coordinate>();
        for (int i = 0;i < edges.size();i++){
            if (Math.abs(coordinate.getY() - edges.get(i).getY()) < tolerant){
                sameY.add(edges.get(i));
            }
        }
        for (int i = 0;i <sameY.size();i++){
            if (sameY.get(i).getX()<coordinate.getX()){
                lessJudge = true;
            }
            if (sameY.get(i).getX()>coordinate.getX()){
                moreJudge = true;
            }
        }
        if (lessJudge&&moreJudge){
            judge = true;
        }
        return judge;
    }

    /**
     * 找到边界值
     * @param border 一组地块坐标
     * @return 东西南北边界
     */
    public static double[] findBorder(ArrayList<Coordinate> border){
        double[] azimuth = new double[4];
        double east = border.get(0).getX();
        double west = border.get(0).getX();
        double north = border.get(0).getY();
        double south = border.get(0).getY();
        for (int i = 1; i < border.size(); i++) {
            if (border.get(i).getX()>east){
                east = border.get(i).getX();
            }
            if (border.get(i).getX()<west){
                west = border.get(i).getX();
            }
            if (border.get(i).getY()<south){
                south = border.get(i).getY();
            }
            if (border.get(i).getY()>north){
                north = border.get(i).getY();
            }
        }
        azimuth[0] = east;
        azimuth[1] = west;
        azimuth[2] = south;
        azimuth[3] = north;
        return azimuth;
    }

    /**
     * 导入数据
     *
     * @param file 文件
     * @return 数据数组
     */
    public static List<String> importFile(File file) {
        List<String> dataList = new ArrayList<String>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                dataList.add(line);
            }
        } catch (Exception e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                    br = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dataList;
    }

    /**
     * 通过字符获取某车某天的地块信息
     * @param data 地块字符center1_border1-border2&center2_border1-border2
     * @return 地块信息数组
     */
    public static List<ArrayList<Coordinate>> getBlockMessage(String data){
        List<ArrayList<Coordinate>> blocks = new ArrayList<>();
        String blocksStr[] = data.split("&");
        for (int i = 0;i < blocksStr.length;i++){
            ArrayList<Coordinate> blockSingle = new ArrayList<>();
            String centerBorders[] = blocksStr[i].split("_");
            String centerXY[] = centerBorders[0].split(",");
            double centerX = Double.parseDouble(centerXY[0]);
            double centerY = Double.parseDouble(centerXY[1]);
            Coordinate center = new Coordinate(centerX, centerY);
            center.setSerial(i);
            blockSingle.add(center);
            String borders[] = centerBorders[1].split("-");
            for (int j = 0;j < borders.length;j++){
                String borderXY[] = borders[j].split(",");
                double borderX = Double.parseDouble(borderXY[0]);
                double borderY = Double.parseDouble(borderXY[1]);
                Coordinate border = new Coordinate(borderX, borderY);
                blockSingle.add(border);
            }
            blocks.add(blockSingle);
        }
        return blocks;
    }

    /**
     * 获取中心点信息
     * @param data 地块字符
     * @return 中心点信息数组
     */
    public static ArrayList<Coordinate> getCenterMessage(String data){
        ArrayList<Coordinate> centers = new ArrayList<>();
        String blocksStr[] = data.split("&");
        for (int i = 0;i < blocksStr.length;i++){
            String centerBorders[] = blocksStr[i].split("_");
            String centerXY[] = centerBorders[0].split(",");
            double centerX = Double.parseDouble(centerXY[0]);
            double centerY = Double.parseDouble(centerXY[1]);
            Coordinate center = new Coordinate(centerX, centerY);
            centers.add(center);
        }
        return centers;
    }

    /**
     * 获取边界点最远距离
     * @param borders 边界点数组
     * @return 最远距离
     */
    public static double getFarthest(ArrayList<Coordinate> borders){
        double distance = 0;
        for (int i = 0;i < borders.size()-1;i++){
            for (int j = i + 1;j < borders.size();j++){
                double distanceSingle = GeoUtils.euclideanDistance(borders.get(i), borders.get(j));
                if (distance < distanceSingle){
                    distance = distanceSingle;
                }
            }
        }
        return distance;
    }

    //绘制坐标点
//    public void start(Stage primaryStage) throws Exception {
//        Figure figure = getFigureWithAxis();
//        //绘制地块和边缘点
//        drawBlocksWithEdge(figure);
//        //按照横坐标排序绘制
//        drawCorrelation(figure);
//        //历史重耕检测
//        generateDetection();
//    }

    public void drawBlocksWithEdge(Figure figure) {
        String fileName = "D:/JavaProjects/BootStrapHistoryCalculator/" +
                "deviceNoTarget_Coordinate2059.txt";
        drawPointFile(figure, fileName);
    }

    public void drawCorrelation(Figure figure) {
        ArrayList<String> timeFile = new ArrayList<>();
        timeFile.add("D:/JavaProjects/BootStrapHistoryCalculator/blockTime2.txt");
        timeFile.add("D:/JavaProjects/BootStrapHistoryCalculator/blockTime1_2.txt");
        timeFile.add("D:/JavaProjects/BootStrapHistoryCalculator/blockTime0.5.txt");
        drawPolylineFile(figure, timeFile);
    }

    public void generateDetection() throws ParseException {
        double repeatedArea = currentDetect() / 666.6666667;
        if (repeatedArea > 0) {
            System.out.println("历史重耕面积:" + repeatedArea + "亩");
        } else {
            System.out.println("不存在历史重耕");
        }
    }

    public double currentDetect() throws ParseException {
        List<Coordinate> currentCenters = getCurrentCentersFromDB();
        double area = 0;
        for (Coordinate currentCenter:currentCenters) {
            area += detection(currentCenter);
        }
        return area;
    }

    public double detection(Coordinate currentCenter) throws ParseException {
        List<Coordinate> centers = getCentersFromDB();
        List<Coordinate> suspicionCenters = filter(centers, currentCenter);
        List<ArrayList<Coordinate>> edges = getEdgesFromCenters(suspicionCenters);
        ArrayList<Coordinate> currentEdge = getEdgeFromCenter(currentCenter);
        ArrayList<Coordinate> tracingPoints = getTracingPointsFromDB(currentCenter);
        List<ArrayList<Coordinate>> repeatedEdges = new ArrayList<>();
        for (int i = 0;i < currentEdge.size();i++) {
            for (int j = 0;j < edges.size();j++) {
                if (isInsidePolygon(currentEdge.get(i), edges.get(j))){
                    repeatedEdges.add(edges.get(j));
                }
            }
        }
        ArrayList<Coordinate> repeatedTracingPoints = new ArrayList<>();
        for (int i = 0;i < tracingPoints.size();i++) {
            for (int j = 0;j < repeatedEdges.size();j++) {
                if (isInsidePolygon(tracingPoints.get(i), repeatedEdges.get(j))){
                    repeatedTracingPoints.add(tracingPoints.get(i));
                }
            }
        }
        return calcBlockArea(repeatedTracingPoints);
    }

    public double detectByEdge(Coordinate currentCenter) throws ParseException {
        List<Coordinate> centers = getCentersFromDB();
        List<Coordinate> suspicionCenters = filter(centers, currentCenter);
        List<ArrayList<Coordinate>> edges = getEdgesFromCenters(suspicionCenters);
        ArrayList<Coordinate> currentEdge = getEdgeFromCenter(currentCenter);
        List<ArrayList<Coordinate>> repeatedEdges = new ArrayList<>();
        List<ArrayList<Coordinate>> commonEdges = new ArrayList<>();
        for (int i = 0;i < currentEdge.size();i++) {
            for (int j = 0;j < edges.size();j++) {
                if (isInsidePolygon(currentEdge.get(i), edges.get(j))){
                    repeatedEdges.add(edges.get(j));
                    ArrayList<Coordinate> singleEdge = new ArrayList<>();
                    for (int k = 0;k < edges.get(j).size();k++){
                        if (isInsidePolygon(edges.get(j).get(k), currentEdge)){
                            singleEdge.add(edges.get(j).get(k));
                        }
                    }
                    commonEdges.add(singleEdge);
                }
            }
        }
        List<ArrayList<Coordinate>> otherCommonEdges = new ArrayList<>();
        for (int i = 0;i < repeatedEdges.size();i++) {
            ArrayList<Coordinate> singleEdge = new ArrayList<>();
            for (int j = 0;j < currentEdge.size();j++) {
                if (isInsidePolygon(currentEdge.get(j), repeatedEdges.get(i))) {
                    singleEdge.add(currentEdge.get(j));
                }
            }
            otherCommonEdges.add(singleEdge);
        }
        for (int i = 0;i < commonEdges.size();i++) {
            commonEdges.get(i).addAll(otherCommonEdges.get(i));
        }
        double area = 0;
        for (int i = 0;i < commonEdges.size();i++) {
            area += getAreaFromEdge(commonEdges.get(i));
        }
        return area;
    }

    public ArrayList<Coordinate> getTracingPointsFromDB(Coordinate currentCenter){
        ArrayList<Coordinate> tracingPoints = new ArrayList<>();
        return tracingPoints;
    }

    /**
     * 测亩仪，需要顺时针排列
     * @param coordinates 边缘点
     * @return 所围面积
     */
    public double getAreaFromEdge(ArrayList<Coordinate> coordinates){
        double area = 0;
        for (int i = 0;i < coordinates.size() - 1;i++){
            area += coordinates.get(i).getX() * coordinates.get(i+1).getY() -
                    coordinates.get(i).getY() * coordinates.get(i+1).getX();
        }
        return area;
    }

    public List<ArrayList<Coordinate>> getEdgesFromCenters(List<Coordinate> suspicionCenters){
        List<ArrayList<Coordinate>> edges = new ArrayList<>();
        for (Coordinate suspicionCenter:suspicionCenters){
            ArrayList<Coordinate> edge = getEdgeFromCenter(suspicionCenter);
            edges.add(edge);
        }
        return edges;
    }

    public ArrayList<Coordinate> getEdgeFromCenter(Coordinate suspicionCenter){
        ArrayList<Coordinate> edges = new ArrayList<>();
        return edges;
    }

    public List<Coordinate> filter(List<Coordinate> centers,Coordinate currentCenter){
        List<Coordinate> suspicionCenters = new ArrayList<>();
        for (Coordinate center:centers){
            double distance = GeoUtils.euclideanDistance(center, currentCenter);
            if (distance < center.getDistance() + currentCenter.getDistance()){
                suspicionCenters.add(center);
            }
        }
        return suspicionCenters;
    }

    public List<Coordinate> getCurrentCentersFromDB(){
        List<Coordinate> currentCenters = new ArrayList<>();
        return currentCenters;
    }

    public List<Coordinate> getCentersFromDB() throws ParseException {
        Date date = new SimpleDateFormat("yyyy-mm-dd").parse("2015-10-01");
        FeatureRecord record = new FeatureRecord("合肥", new java.sql.Date(date.getTime()), 0);
        return record.getCoordinates();
    }

    /**
     * 绘制文件
     * @param figure 图像
     * @param timeFile 文件数组
     */
    public void drawPolylineFile(Figure figure, ArrayList<String> timeFile){
        for (int i = 0;i < timeFile.size();i++){
            List<String> blockTimeLists = importFile(new File(timeFile.get(i)));
            figure.addDrawable(getTimeSizePolyline(blockTimeLists, i));
        }
    }

    /**
     * 获取单独折线
     * @param blockTimeLists 坐标字符串
     * @param index 颜色序号
     * @return 折线
     */
    public PolylineDrawable getTimeSizePolyline(List<String> blockTimeLists, int index){
        ArrayList<SerialPoint> serialPoints = getSerialPointsFromTimeLists(blockTimeLists);
        SerialPoint serialPointArray[] = sortByX(toArray(serialPoints));
        ArrayList<SerialPoint> points = toList(serialPointArray);
        PolylineDrawable polylineDrawable = new PolylineDrawable(points);
        switch (index){
            case 0: polylineDrawable.setColor(Color.BLACK);
                break;
            case 1: polylineDrawable.setColor(Color.BLUE);
                break;
            case 2: polylineDrawable.setColor(Color.GREEN);
                break;
            case 3: polylineDrawable.setColor(Color.GRAY);
                break;
            default:polylineDrawable.setColor(Color.BLACK);
        }
        return polylineDrawable;
    }

    /**
     * 将字符串数组转换为坐标点
     * @param blockTimeLists 字符串数组
     * @return 折线坐标点
     */
    public ArrayList<SerialPoint> getSerialPointsFromTimeLists(List<String> blockTimeLists){
        ArrayList<SerialPoint> serialPoints = new ArrayList<>();
        for (String blockTime:blockTimeLists){
            String timeArray[] = blockTime.split(",");
            serialPoints.add(new SerialPoint(Double.valueOf(timeArray[0])/1000, Double.valueOf(timeArray[1])));
        }
        return serialPoints;
    }

    /**
     * 将动态数组转换为数组
     * @param serialPoints 动态数组
     * @return 数组
     */
    public SerialPoint[] toArray(ArrayList<SerialPoint> serialPoints){
        SerialPoint serialPointArray[] = new SerialPoint[serialPoints.size()];
        for (int i = 0;i < serialPointArray.length;i++){
            serialPointArray[i] = serialPoints.get(i);
        }
        return serialPointArray;
    }

    /**
     * 坐标点按照X坐标排序
     * @param serialPointArray 坐标点数组
     * @return 坐标点数组
     */
    public SerialPoint[] sortByX(SerialPoint[] serialPointArray){
        for (int i = 0;i < serialPointArray.length - 1;i++){
            for (int j = i+1;j < serialPointArray.length;j++){
                if (serialPointArray[i].getX()>serialPointArray[j].getX()){
                    SerialPoint temp = serialPointArray[i];
                    serialPointArray[i] = serialPointArray[j];
                    serialPointArray[j] = temp;
                }
            }
        }
        return serialPointArray;
    }

    /**
     * 将数组转换为动态数组
     * @param serialPointArray 数组
     * @return 动态数组
     */
    public ArrayList<SerialPoint> toList(SerialPoint[] serialPointArray){
        ArrayList<SerialPoint> points = new ArrayList<>();
        for (int i = 0;i < serialPointArray.length;i++){
            points.add(serialPointArray[i]);
        }
        return points;
    }

    public void storeList() {
        List<ArrayList<Coordinate>> centers = new ArrayList<>();//几何中心点
        List<ArrayList<Double>> distances = new ArrayList<>();//对应距离
        List<String> blockLists = importFile(new File("D:/JavaProjects/" +
                "BootStrapHistoryCalculator/device10223.txt"));
//      得到当前设备中心点和对应的最远距离
        for (int i = 0;i < blockLists.size();i++){
            ArrayList<Coordinate> centerSingleDay = getCenterMessage(blockLists.get(i));
            ArrayList<Double> distanceFarthest = new ArrayList<>();
            List<ArrayList<Coordinate>> blocks = getBlockMessage(blockLists.get(i));
            for (int j = 0;j <blocks.size();j++){
                double distance = getFarthest(blocks.get(j));
                distanceFarthest.add(distance);
            }
            for (int j = 0;j < centerSingleDay.size();j++){
                centerSingleDay.get(j).setDaySerial(i);
                centerSingleDay.get(j).setBlockSerial(j);
            }
            centers.add(centerSingleDay);
            distances.add(distanceFarthest);
        }
    }

    public void insertSql(ArrayList<Coordinate> centers, List<ArrayList<Coordinate>> blocks) throws ParseException {
        for (int i = 0;i < centers.size();i++) {
            ArrayList<Coordinate> block = blocks.get(i);
            Coordinate center = centers.get(i);
            String edge = "";
            for (int j = 0;j < block.size() - 1;j++) {
                edge += block.get(j).getX() + "," + block.get(j).getY() + "-";
            }
            edge += block.get(block.size() - 1);
            String sql = "INSERT INTO rotation (deviceNo, coordinateX, coordinateY, edge, date)";
            Date date = new SimpleDateFormat("yyyy-mm-dd").parse("2015-10-01");
            FeatureRecord record = new FeatureRecord("合肥", new java.sql.Date(date.getTime()), 0);
            record.execute(sql);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        List<ArrayList<Coordinate>> centers = new ArrayList<>();//几何中心点
        List<ArrayList<Double>> distances = new ArrayList<>();//对应距离
        //导入历史耕作列表，1列或多列
        List<String> blockLists = importFile(new File("D:/JavaProjects/" +
                "BootStrapHistoryCalculator/device10223.txt"));
        //导入待判断的耕作列表,通常为1列，即更新当天
        List<String> blockTarget = importFile(new File("D:/JavaProjects/" +
                "BootStrapHistoryCalculator/deviceNoTarget0211.txt"));
        //得到当前设备中心点和对应的最远距离
        for (int i = 0;i < blockLists.size();i++){
            ArrayList<Coordinate> centerSingleDay = getCenterMessage(blockLists.get(i));
            ArrayList<Double> distanceFarthest = new ArrayList<>();
            List<ArrayList<Coordinate>> blocks = getBlockMessage(blockLists.get(i));
            for (int j = 0;j <blocks.size();j++){
                double distance = getFarthest(blocks.get(j));
                distanceFarthest.add(distance);
            }
            for (int j = 0;j < centerSingleDay.size();j++){
                centerSingleDay.get(j).setDaySerial(i);
                centerSingleDay.get(j).setBlockSerial(j);
            }
            centers.add(centerSingleDay);
            distances.add(distanceFarthest);
        }
        //得到目标设备的中心点、边缘点和对应的最远距离
        ArrayList<Coordinate> centerTarget = getCenterMessage(blockTarget.get(0));//待判断的地块中心点
        List<ArrayList<Coordinate>> blockCurrent = getBlockMessage(blockTarget.get(0));//待判断的地块边缘点
        ArrayList<Double> distancesTarget = new ArrayList<>();//对应的最远距离
        for (int i = 0;i < blockCurrent.size();i++){
            double distanceSingle = getFarthest(blockCurrent.get(i));
            distancesTarget.add(distanceSingle);
        }
        ArrayList<Coordinate> centersSuspicion = new ArrayList<>();
        //通过距离判断找出可疑中心点
        for (int i = 0;i < centerTarget.size();i++){
            for (int j = 0;j < centers.size();j++){
                for (int k = 0;k < centers.get(j).size();k++){
                    double centersDistance = GeoUtils.euclideanDistance(centers.get(j).get(k),centerTarget.get(i));
                    double farthestDistance = distances.get(j).get(k) + distancesTarget.get(i);
                    if (centersDistance < farthestDistance){
                        centersSuspicion.add(centers.get(j).get(k));
                    }
                }
            }
        }
        if (centersSuspicion.size() > 0) {
            List<ArrayList<Coordinate>> blockSuspicion = new ArrayList<>();//可疑地块边缘点
            ArrayList<Index> indexes = new ArrayList<>();
            ArrayList<Index> indexesSuspicion = new ArrayList<>();
            //判断目标中心点是否在边缘点的多边形中，记录当前设备重耕地块号
            boolean isRepeated = false;
            ArrayList<Integer> blockSerials = new ArrayList<>();//记录重复地块号
            for (int i = 0; i < centersSuspicion.size(); i++) {
                int daySerial = centersSuspicion.get(i).getDaySerial();//记录日期
                int blockSerial = centersSuspicion.get(i).getBlockSerial();//记录地块号
                indexesSuspicion.add(new Index(daySerial, blockSerial));
                blockSuspicion = getBlockMessage(blockLists.get(daySerial));
                ArrayList<Coordinate> blockCurrentSuspicion = blockSuspicion.get(blockSerial);
                for (int j = 0; j < centerTarget.size(); j++) {
                    if (isInsidePolygon(centerTarget.get(j), blockCurrentSuspicion)) {
                        isRepeated = true;
                        blockSerials.add(blockSerial);
                        indexes.add(new Index(daySerial, blockSerial));
                    }
                }
            }
            //通过网格算法计算重耕面积
            if (isRepeated) {
                List<ArrayList<Coordinate>> edgeSuspicion = getIndexEdges(blockLists, indexesSuspicion);
                List<ArrayList<Coordinate>> edgeRepeated = getIndexEdges(blockLists, indexes);
                System.out.println("发现与设备号" + "存在历史重耕");
                //目标设备的轨迹点坐标
                List<String> coordinateLists = importFile(new File("D:/JavaProjects/" +
                        "BootStrapHistoryCalculator/deviceNoTarget_Coordinate0212.txt"));
                //存储带有地块编号的轨迹点
                ArrayList<Coordinate> coordinates = getCoordinatesFromStrings(coordinateLists);
                ArrayList<Coordinate> coordinateRepeated = new ArrayList<>();//存储重复轨迹点
                //找到重耕轨迹点
                for (int i = 0; i < coordinates.size(); i++) {
                    for (int j = 0; j < blockSerials.size(); j++) {
                        if (isInsidePolygon(coordinates.get(i), blockSuspicion.get(blockSerials.get(j)))) {
                            coordinateRepeated.add(coordinates.get(i));
                        }
                    }
                }
                //按照地块结构存储
                ArrayList<ArrayList<Coordinate>> arrayRepeated = getBlockArray(coordinateRepeated);
                //重耕地块数
                System.out.println("目标设备重耕地块数:" + arrayRepeated.size());
                //分地块计算重复面积，若计算总面积也需要分地块
                double areaRepeated = 0;
                for (int i = 0;i < arrayRepeated.size();i++){
                    double areaSingle = calcBlockArea(arrayRepeated.get(i));
                    areaRepeated += areaSingle;
                }
                System.out.println("历史重耕面积为：" + areaRepeated / 666.6666667 + "亩");

                //使用原画图工具画历史重耕图
                ArrayList<Coordinate> coordinateTests = new ArrayList<>();//存储边缘点
                //在blockSuspicion中按照blockSerials中的编号找出确定重复的边缘点
                for (int i = 0;i < blockSerials.size();i++){
                    coordinateTests.addAll(blockSuspicion.get(blockSerials.get(i)));
                }
                coordinates = setColourSerial(coordinates, 1);//所有轨迹点设为1粉色
                coordinateRepeated = setColourSerial(coordinateRepeated, 2);//重耕轨迹点设为2红色
                coordinateTests = setColourSerial(coordinateTests, 3);//重复边缘点设为3黑色
                coordinates.addAll(coordinateTests);
                coordinates.addAll(coordinateRepeated);
                drawRepeatedBlockOriginally(coordinates);

                //矢量绘制重耕点
                ArrayList<SerialPoint> points = getPointsFromCoordinates(coordinates, 0);//轨迹点
                ArrayList<SerialPoint> pointsRepeated = getPointsFromCoordinates(coordinateRepeated, 1);//重耕点
                ArrayList<SerialPoint> pointsTest = getPointsFromCoordinates(coordinateTests, 2);//重耕边缘点
                points.addAll(pointsRepeated);
                points.addAll(pointsTest);
                ArrayList<ArrayList<SerialPoint>> pointsSuspicion = getEdgesFromCoordinates(blockSuspicion);//可疑边缘点集合
                ArrayList<ArrayList<SerialPoint>> edgesSuspicion = getEdgesFromCoordinates(edgeSuspicion);
                ArrayList<ArrayList<SerialPoint>> edgesRepeated = getEdgesFromCoordinates(edgeRepeated);
                //将可疑边缘点添加到points和pointsSuspicion中
                points = addToPoints(points, edgesRepeated);
                points = addToPoints(points, edgesSuspicion);
                Figure figure = getFigureWithAxis();
                drawPoints(figure, points);
                drawPolyline(figure, edgesSuspicion);
                drawPolygon(figure, edgesRepeated);
            } else {
                System.out.println("未发现当前设备的历史重耕问题");
            }
        }else {
            System.out.println("未发现可疑地块");
        }
    }

    public static ArrayList<SerialPoint> addToPoints(ArrayList<SerialPoint> points, ArrayList<ArrayList<SerialPoint>> edges){
        for (int i = 0;i < edges.size();i++){
            points.addAll(edges.get(i));
        }
        return points;
    }

    public static void drawPolygon(Figure figure, ArrayList<ArrayList<SerialPoint>> pointsSuspicion){
        for (int i = 0;i < pointsSuspicion.size();i++){
            PolygonDrawable polylineDrawable = new PolygonDrawable(pointsSuspicion.get(i));//使用多边形
            polylineDrawable.setColor(new Color(1, 0.75, 0.79, 0.2));//半透明绿色多边形
            figure.addDrawable(polylineDrawable);
        }
    }

    public static void drawPolyline(Figure figure, ArrayList<ArrayList<SerialPoint>> pointsSuspicion){
        for (int i = 0;i < pointsSuspicion.size();i++){
            PolylineDrawable polylineDrawable = new PolylineDrawable(pointsSuspicion.get(i));//使用折线
            polylineDrawable.setColor(Color.BLACK);
            figure.addDrawable(polylineDrawable);
        }
    }

    public static ArrayList<ArrayList<SerialPoint>> getEdgesFromCoordinates(List<ArrayList<Coordinate>> blockSuspicion){
        ArrayList<ArrayList<SerialPoint>> pointsSuspicion = new ArrayList<>();
        for (int i = 0;i < blockSuspicion.size();i++){
            ArrayList<SerialPoint> serialPoints = new ArrayList<>();
            for (int j = 1;j < blockSuspicion.get(i).size() ;j++){
                SerialPoint point = new SerialPoint(blockSuspicion.get(i).get(j).getX(), blockSuspicion.get(i).get(j).getY());
                serialPoints.add(point);
            }
            pointsSuspicion.add(serialPoints);
        }
        return pointsSuspicion;
    }

    public static List<ArrayList<Coordinate>> getIndexEdges(List<String> blockLists, ArrayList<Index> indexes){
        List<ArrayList<Coordinate>> edgeRepeated = new ArrayList<>();
        for (Index index:indexes){
            List<ArrayList<Coordinate>> singleDay = getBlockMessage(blockLists.get(index.getDaySerial()));
            edgeRepeated.add(singleDay.get(index.getBlockSerial()));
        }
        return edgeRepeated;
    }

    /**
     * 原始绘制:设置颜色序号
     * @param coordinates 坐标点
     * @param serial 序号
     * @return 坐标点
     */
    public static ArrayList<Coordinate> setColourSerial(ArrayList<Coordinate> coordinates, int serial){
        for (int i = 0;i < coordinates.size();i++){
            coordinates.get(i).setColourSerial(serial);//目标设备轨迹点
        }
        return coordinates;
    }

    /**
     * coordinate转换为point
     * @param coordinates 坐标点
     * @param serial 颜色序号
     * @return 绘制点
     */
    public static ArrayList<SerialPoint> getPointsFromCoordinates(ArrayList<Coordinate> coordinates,int serial){
        ArrayList<SerialPoint> points = new ArrayList<>();
        for (Coordinate coordinate:coordinates){
            SerialPoint point = new SerialPoint(coordinate.getX(), coordinate.getY());
            point.setGraphicSerial(serial);
            points.add(point);
        }
        return points;
    }

    /**
     * 利用文件绘制坐标
     * @param figure 图像框
     * @param fileName 坐标文件
     */
    public static void drawPointFile(Figure figure, String fileName){
        List<String> coordinateLists = importFile(new File(fileName));
        ArrayList<Coordinate> coordinates = getCoordinatesFromStrings(coordinateLists);
        ArrayList<ArrayList<Coordinate>> multipleBlocks = new ArrayList<>();
        drawVectorPoints(figure, coordinates);
    }

    /**
     * 建立坐标轴图像框
     * @return 图像框
     */
    public static Figure getFigureWithAxis(){
        Figure figure = new Figure(600,600);
        Axis axis = new Axis();
        figure.addDrawable(axis);
        return figure;
    }

    /**
     * 利用字符得到坐标数组
     * @param coordinateLists 字符数组
     * @return 坐标数组
     */
    public static ArrayList<Coordinate> getCoordinatesFromStrings(List<String> coordinateLists){
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        for (int i = 0; i < coordinateLists.size(); i++) {
            String targetXY[] = coordinateLists.get(i).split(",");
            double targetX = Double.parseDouble(targetXY[0]);
            double targetY = Double.parseDouble(targetXY[1]);
            int blockNum = Integer.parseInt(targetXY[2]);
            Coordinate target = new Coordinate(targetX, targetY);
            target.setLandBlock(blockNum);
            coordinates.add(target);
        }
        return coordinates;
    }

    /**
     * 利用coordinate画图
     * @param figure 图像框
     * @param coordinates 坐标
     */
    public static void drawVectorPoints(Figure figure, ArrayList<Coordinate> coordinates){
        ArrayList<SerialPoint> points = new ArrayList<>();
        for (Coordinate coordinate:coordinates){
            SerialPoint point = new SerialPoint(coordinate.getX(), coordinate.getY());
            point.setGraphicSerial(coordinate.getLandBlock());
            points.add(point);
        }
        drawPoints(figure, points);
    }

    /**
     * 利用point画图
     * @param figure 图像框
     * @param points 绘制点
     */
    public static void drawPoints(Figure figure, ArrayList<SerialPoint> points){
        Axis axis = new Axis();
        figure.addDrawable(axis);
        double ratio[] = getRatio(points);
        for (SerialPoint serialPoint:points){
            serialPoint.setX((serialPoint.getX() - ratio[2]) / ratio[0]);
            serialPoint.setY((serialPoint.getY() - ratio[3]) / ratio[1]);
            PointDrawable pointDrawable = new PointDrawable(serialPoint);
            switch (serialPoint.getGraphicSerial()){
                case 0:
                    pointDrawable.setColor(Color.YELLOWGREEN);
                    break;
                case 1:
                    pointDrawable.setColor(Color.RED);
                    break;
                case 2:
                    pointDrawable.setColor(Color.SADDLEBROWN);
                    break;
                case 3:
                    pointDrawable.setColor(Color.BLUE);
                    break;
                case 4:
                    pointDrawable.setColor(Color.PINK);
                    break;
                case 5:
                    pointDrawable.setColor(Color.DEEPSKYBLUE);
                    break;
                case 6:
                    pointDrawable.setColor(Color.ORANGE);
                    break;
                case 7:
                    pointDrawable.setColor(Color.CYAN);
                    break;
                case 100:
                    pointDrawable.setColor(Color.BLACK);
                default:
                    pointDrawable.setColor(Color.BLACK);
            }
            figure.addDrawable(pointDrawable);
        }
        figure.setTitle("矢量绘制");
    }

    /**
     * 获取缩放比例
     * @param points 绘制点
     * @return 比例数组
     */
    public static double[] getRatio(ArrayList<SerialPoint> points){
        double[] ratio = new double[4];
        double maxX = 0;
        double maxY = 0;
        double minX = maxDistance;
        double minY = maxDistance;
        for (int i = 0;i<points.size();i++){
            if (points.get(i).getX()>maxX){
                maxX = points.get(i).getX();
            }
            if (points.get(i).getY()>maxY){
                maxY = points.get(i).getY();
            }
            if (points.get(i).getX()<minX){
                minX = points.get(i).getX();
            }
            if (points.get(i).getY()<minY){
                minY = points.get(i).getY();
            }
        }
        double diffY = maxY - minY;
        double diffX = maxX - minX;
        ratio[0] = diffX/600.0;
        ratio[1] = diffY/600.0;
        ratio[2] = minX;
        ratio[3] = minY;
        return ratio;
    }
}