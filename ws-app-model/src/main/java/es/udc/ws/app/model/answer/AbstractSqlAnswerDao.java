package es.udc.ws.app.model.answer;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSqlAnswerDao implements SqlAnswerDao {
    protected AbstractSqlAnswerDao() {
    }

    @Override
    public Answer find(Connection connection, Long answerId)
            throws InstanceNotFoundException {
        /* Create "queryString". */
        String queryString = "SELECT eventId, employeeEmail, attending,"
                + " answerDate FROM Answer WHERE answerId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, answerId.longValue());

            /* Execute query. */
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new InstanceNotFoundException(answerId,
                        Answer.class.getName());
            }

            /* Get results. */
            i = 1;
            Long eventId = resultSet.getLong(i++);
            String employeeEmail = resultSet.getString(i++);
            boolean attending = resultSet.getBoolean(i++);
            Timestamp answerDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime answerDate = answerDateAsTimestamp.toLocalDateTime();


            return new Answer(answerId, eventId, employeeEmail, attending, answerDate);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void remove(Connection connection, Long answerId)
            throws InstanceNotFoundException {

        /* Create "queryString". */
        String queryString = "DELETE FROM Answer WHERE" + " answerId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, answerId);

            /* Execute query. */
            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new InstanceNotFoundException(answerId,
                        Answer.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Answer> findByEmail(Connection connection, String employeeEmail, boolean onlyAffirmative) {
        /* Create "queryString". */
        String queryString = "SELECT answerId, eventId, employeeEmail, attending,"
                + " answerDate FROM Answer WHERE employeeEmail = ?";


        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setString(i++, employeeEmail);

            /* Execute query. */
            ResultSet resultSet = preparedStatement.executeQuery();

            /* Read answers. */
            List<Answer> answers = new ArrayList<Answer>();

            while (resultSet.next()) {

                i = 1;
                Long answerId = Long.valueOf(resultSet.getLong(i++));
                Long eventId = Long.valueOf(resultSet.getLong(i++));
                String employeeEmail2 = resultSet.getString(i++);
                boolean attending = resultSet.getBoolean(i++);
                Timestamp answerDateAsTimestamp = resultSet.getTimestamp(i++);
                LocalDateTime answerDate = answerDateAsTimestamp.toLocalDateTime();

                if(!onlyAffirmative || attending){
                    answers.add(new Answer(answerId, eventId, employeeEmail2, attending,
                            answerDate));
                }

            }

            /* Return answers. */
            return answers;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
