package es.udc.ws.app.model.answer;

import java.sql.*;

public class Jdbc3CcSqlAnswerDao extends AbstractSqlAnswerDao {

    @Override
    public Answer create(Connection connection, Answer answer) {
        /* Create "queryString". */
        String queryString = "INSERT INTO Answer"
                + " (eventId, employeeEmail, attending, answerDate)"
                + " VALUES (?, ?, ?, ?)";


        try (PreparedStatement preparedStatement = connection.prepareStatement(
                queryString, Statement.RETURN_GENERATED_KEYS)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, answer.getEventId());
            preparedStatement.setString(i++, answer.getEmployeeEmail());
            preparedStatement.setBoolean(i++, answer.isAttending());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(answer.getAnswerDate()));

            /* Execute query. */
            preparedStatement.executeUpdate();

            /* Get generated identifier. */
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (!resultSet.next()) {
                throw new SQLException(
                        "JDBC driver did not return generated key.");
            }
            Long answerId = resultSet.getLong(1);


            return new Answer(answerId, answer.getEventId(), answer.getEmployeeEmail(), answer.isAttending(),
                    answer.getAnswerDate());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
