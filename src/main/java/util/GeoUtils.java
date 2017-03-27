package util;

import model.Point;
import model.Coordinate;

import java.util.ArrayList;

/**
 * 地理工具
 */
public class GeoUtils {
    public final static int COORDINATE_MULTIPLE = 3600000;

    /**
     * 获取不同地块点
     * @param coordinates
     * @return
     */
    public static ArrayList<Coordinate> getDiffBlockCoor(ArrayList<Coordinate> coordinates){
        ArrayList<Coordinate> blockNum = new ArrayList<>();
        blockNum.add(coordinates.get(0));
        for (int i = 1;i<coordinates.size();i++){
            boolean repeated = false;
            for (int j = 0;j<blockNum.size();j++){
                if (coordinates.get(i).getLandBlock()==blockNum.get(j).getLandBlock()){
                    repeated = true;
                    break;
                }
            }
            if (!repeated){
                blockNum.add(coordinates.get(i));
            }
        }
        return blockNum;
    }
    /**
     * @link http://stackoverflow.com/questions/837872/calculate-geoDistance-in-meters-when-you-know-longitude-and-latitude-in-java
     */
    public static double geoDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000;               // 地球平均半径，单位：米
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    /**
     * 计算经纬度距离
     */
    public static double geoDistance(Point a, Point b) {
        return geoDistance(a.getLatitude(), a.getLongitude(), b.getLatitude(), b.getLongitude());
    }

    /**
     * 计算欧氏距离
     */
    public static double euclideanDistance (Coordinate a, Coordinate b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    /**
     * 粗略地判断坐标点是否在中国国境以内
     */
    public static boolean isInsideChina (Point point) {
        return point.getLatitude() > 4 && point.getLatitude() < 54 &&
                point.getLongitude() > 72 && point.getLongitude() < 136;
    }

    /**
     * 将经纬度转换为XY坐标
     * @link http://411431586.blog.51cto.com/4043583/743305
     */
    public static Coordinate transformToXY (Point point) {
        double longitude = point.getLongitude();
        double latitude = point.getLatitude();
        int zoneWidth = 6;                                              // 6度带带宽
        int projNo = (int)(longitude / zoneWidth);                      // 6度带带号
        double iPI = Math.PI / 180;                                     // 弧度单位
        double a = 6378137.0;                                           // WGS-84椭球体长半轴长度
        double b = 6356752.3142;                                        // WGS-84椭球体短半轴长度
        double L = longitude * iPI ;                                    // 经度转换为弧度
        double B = latitude * iPI ;                                     // 纬度转换为弧度
        double e1 = Math.sqrt(a * a - b * b) / a;                       // 椭球第一偏心率
        double e2 = Math.sqrt(a * a - b * b) / b;                       // 椭球第二偏心率
        double L0 = (projNo * zoneWidth + zoneWidth / 2) * iPI;         // 对应带的中央子午线到本初子午线的弧度

        double T = Math.pow(Math.tan(B), 2);
        double C = Math.pow(Math.cos(B) * e2, 2);
        double A = (L - L0) * Math.cos(B);
        double N = a / Math.sqrt(1.0 - Math.pow(e1 * Math.sin(B), 2));  // 卯酉圈曲率半径
        double M = a * (                                                // 子午线弧长
                (1 - Math.pow(e1, 2) / 4 - 3 * Math.pow(e1, 4) / 64 - 5 * Math.pow(e1, 6) / 256) * B
              - (3 * Math.pow(e1, 2) / 8 + 3 * Math.pow(e1, 4) / 32 + 45 * Math.pow(e1, 6) / 1024) * Math.sin(2 * B)
              + (15 * Math.pow(e1, 4) / 256 + 45 * Math.pow(e1, 6) / 1024) * Math.sin(4 * B)
              - (35 * Math.pow(e1, 6) / 3072) * Math.sin(6 * B)
        );

        double Y0 = 1000000L * (projNo + 1) + 500000L;                  // Y坐标转换到对应的带内
        double X = M + N * Math.tan(B) * (Math.pow(A, 2) / 2 + (5 - T + 9 * C + 4 * Math.pow(C, 2)) * Math.pow(A, 4) / 24 + (61 - 58 * T + Math.pow(T, 2) + 270 * C - 330 * T * C) * Math.pow(A, 6) / 720);
        double Y = Y0 + N * (A + (1 - T + C) * Math.pow(A, 3) / 6 + (5 - 18 * T + Math.pow(T, 2) + 14 * C - 58 * T * C) * Math.pow(A, 5) / 120);

        return new Coordinate(X, Y);
    }

    /*
     * 将经纬度转换为XY坐标
     */
    public static double[] transform(String pp[]){
        double longitude = Double.parseDouble(pp[1]);
        double latitude = Double.parseDouble(pp[0]);
        int ProjNo;
        int ZoneWide;//带宽
        double longitude1,latitude1,longitude0,X0,Y0, xval,yval; double a,f, e2,ee, NN, T,C,A, M, iPI;//latitude0
        iPI = 0.0174532925199433; ////3.1415926535898/180.0;
        ZoneWide = 6; //6度带宽
        a=6378245.0; f=1.0/298.3; //54年北京坐标系参数//a=6378140.0; f=1/298.257; //80年西安坐标系参数
        ProjNo = (int)(longitude / ZoneWide) ;
        longitude0 = ProjNo * ZoneWide + ZoneWide / 2;
        longitude0 = longitude0 * iPI ;
        longitude1 = longitude * iPI ; //经度转换为弧度
        latitude1 = latitude * iPI ; //纬度转换为弧度
        e2 = 2*f-f*f;
        ee = e2*(1.0-e2);
        NN = a/Math.sqrt(1.0-e2*Math.sin(latitude1)*Math.sin(latitude1));
        T = Math.tan(latitude1)*Math.tan(latitude1);
        C = ee*Math.cos(latitude1)*Math.cos(latitude1);
        A = (longitude1-longitude0)*Math.cos(latitude1);
        M = a*((1-e2/4-3*e2*e2/64-5*e2*e2*e2/256)*latitude1-(3*e2/8+3*e2*e2/32+45*e2*e2*e2/1024)*Math.sin(2*latitude1)+(15*e2*e2/256+45*e2*e2*e2/1024)*Math.sin(4*latitude1)-(35*e2*e2*e2/3072)*Math.sin(6*latitude1));
        xval = NN*(A+(1-T+C)*A*A*A/6+(5-18*T+T*T+72*C-58*ee)*A*A*A*A*A/120); yval = M+NN*Math.tan(latitude1)*(A*A/2+(5-T+9*C+4*C*C)*A*A*A*A/24+(61-58*T+T*T+600*C-330*ee)*A*A*A*A*A*A/720);
        X0 =1000000L*(ProjNo+1)+500000L;
        Y0 = 0;
        xval = xval+X0; yval = yval+Y0;
        double[] output = new double[2];
        output[0] = xval;
        output[1] = yval;
        return output;
    }
    public static double distance (double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return earthRadius * c;
    }
    public static double distance (Point a, Point b) {
        return distance(a.getLatitude(), a.getLongitude(), b.getLatitude(), b.getLongitude());
    }
}
