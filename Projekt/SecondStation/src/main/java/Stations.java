import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/*
 * Represents a charging station entity for storing and retrieving station data.
 *
 * Usage:
 * - For CRUD operations in the database (Annotations).
 *
 * Interaction:
 * - Works with various controllers to fetch and manage station data.
 */

@Entity //Marks this class as a JPA entity, indicating it will be mapped to a table in the database.
public class Stations {

    @Id
    private int id;
    private String dbUrl;
    private Double lat;
    private Double lng;

    public int getId() {
        return id;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }
}
