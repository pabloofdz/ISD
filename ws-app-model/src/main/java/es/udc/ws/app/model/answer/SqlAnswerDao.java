package es.udc.ws.app.model.answer;

import es.udc.ws.util.exceptions.InstanceNotFoundException;
import java.sql.Connection;
import java.util.List;

public interface SqlAnswerDao {
    //Para las pruebas
    public Answer find(Connection connection, Long answerId)
            throws InstanceNotFoundException;

    public Answer create(Connection connection, Answer answer);

    // Necesario para buscar respuestas de un empleado
    public List<Answer> findByEmail(Connection connection, String employeeEmail, boolean onlyAffirmative);
    // Necesario para las pruebas
    public void remove(Connection connection, Long answerId)
            throws InstanceNotFoundException;
}

