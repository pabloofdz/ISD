-- ----------------------------------------------------------------------------
-- Model
-------------------------------------------------------------------------------

DROP TABLE Answer;
DROP TABLE Event;

-- --------------------------------- Event ------------------------------------
CREATE TABLE Event( eventId BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) COLLATE latin1_bin NOT NULL,
    description VARCHAR(1024) COLLATE latin1_bin NOT NULL,
    celebrationDate DATETIME NOT NULL,
    duration FLOAT NOT NULL,
    creationDate DATETIME NOT NULL,
    canceled BIT NOT NULL,
    employeesAttending INT NOT NULL,
    employeesNotAttending INT NOT NULL,

    CONSTRAINT EventPK PRIMARY KEY(eventId),
    --CONSTRAINT validCelebrationDate CHECK (celebrationDate > creationDate), Lo quitamos porque sino NO podemos meter un evento pasado en las pruebas. Mejor sería tener esta línea para mayor seguridad
    CONSTRAINT validDuration CHECK ( duration > 0.0),
    CONSTRAINT validEmployeesAttending CHECK ( employeesAttending >= 0 ),
    CONSTRAINT validEmployeesNotAttending CHECK ( employeesNotAttending >= 0 ) )  ENGINE = InnoDB;

-- --------------------------------- Answer ------------------------------------

CREATE TABLE Answer ( answerId BIGINT NOT NULL AUTO_INCREMENT,
    eventId BIGINT NOT NULL,
    employeeEmail VARCHAR(40) COLLATE latin1_bin NOT NULL,
    attending BIT NOT NULL,
    answerDate DATETIME NOT NULL,

    CONSTRAINT AnswerPK PRIMARY KEY(answerId),
    CONSTRAINT AnswerEventIdFK FOREIGN KEY(eventId)
        REFERENCES Event(eventId) ON DELETE CASCADE ) ENGINE = InnoDB;