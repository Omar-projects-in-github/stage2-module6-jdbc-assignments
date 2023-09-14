package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "INSERT INTO myusers (firstname, lastname, age) VALUES (?, ?, ?)";
    private static final String updateUserSQL = "UPDATE myusers SET firstname = ?, lastname = ?, age = ?";
    private static final String deleteUser = "DELETE FROM myusers WHERE id = ?";
    private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE id = ?";
    private static final String findUserByNameSQL = "SELECT * FROM myusers WHERE firstname = ?";
    private static final String findAllUserSQL = "SELECT * FROM myusers";

    public Long createUser(User user) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        finally {
            closeResources();
        }
        return null;
    }

    public User findUserById(Long userId) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(findUserByIdSQL);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        finally {
            closeResources();
        }
        return null;
    }

    public User findUserByName(String userName) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(findUserByNameSQL);
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        finally {
            closeResources();
        }
        return null;
    }

    public List<User> findAllUser() {
        List<User> userList = new ArrayList<>();
        try {
            connection = CustomDataSource.getInstance().getConnection();
            st = connection.createStatement();
            ResultSet rs = st.executeQuery(findAllUserSQL);

            while (rs.next()) {
                userList.add(extractUserFromResultSet(rs));
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        finally {
            closeResources();
        }
        return userList;
    }

    public User updateUser(User user) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(updateUserSQL);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.setLong(4, user.getId());
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0)
                return user;
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        finally {
            closeResources();
        }
        return null;
    }

    private void deleteUser(Long userId) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(deleteUser);
            ps.setLong(1, userId);
            ps.executeUpdate();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        finally {
            closeResources();
        }
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String firstName = rs.getString("firstname");
        String lastName = rs.getString("lastname");
        int age = rs.getInt("age");
        return new User(id, firstName, lastName, age);
    }

    private void closeResources() {
        try {
            if (ps != null)
                ps.close();
            if (st != null)
                st.close();
            if (connection != null)
                connection.close();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
