package connection;

import model.Coordinate;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FeatureRecord extends DBModel{

    private List<Coordinate> coordinates;
    private String location;
    private Date date;
    private int type;
    private Coordinate coordinate;
    private final int scale = 1000;

    public FeatureRecord (String location, Date date, int type){
        this.location = location;
        this.date = date;
        this.type = type;
    }

    public List<Coordinate> getCoordinates () {
        if (coordinates != null)
            return coordinates;
        coordinates = new ArrayList<>();
        String sql = String.format(
                "SELECT landBlock,\n" +
                        " coordinateX,\n" +
                        " coordinateY,\n" +
                        "FROM dbo.feature_points\n" +
                        "WHERE location = %s\n" +
                        " AND type = %d\n" +
                        " AND date = %s\n", location, type, date);
        executeQuery(sql, new OnQueryResultListener() {
            @Override
            public void onResult(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    addPoint(rs);
                }
            }
        });
        return coordinates;
    }

    private void addPoint (ResultSet rs) throws SQLException {
        double coordinateX = rs.getInt("coordinateX") / scale;
        double coordinateY = rs.getInt("coordinateY") / scale;
        int landBlock = rs.getInt("landBlock");
        Coordinate coordinate = new Coordinate(coordinateX, coordinateY);
        coordinate.setLandBlock(landBlock);
        coordinates.add(coordinate);
    }

    @Override
    public boolean commit() {
        String sql = String.format(
                "INSERT INTO dbo.feature_points(coordinateX,coordinateY,landBlock,date,location)\n" +
                        "VALUES (%d, %d, %d, '%s', %s)",
                (int)coordinate.getX() * scale,
                (int)coordinate.getY() * scale,
                coordinate.getLandBlock(),
                date,
                location);
        return execute(sql);
    }
}
