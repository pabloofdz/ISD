namespace java es.udc.ws.app.thrift

struct ThriftEventDto {
    1: i64 eventId
    2: string name
    3: string description
    4: string celebrationDate
    5: double duration
    6: bool canceled
    7: i32 employeesAttending
    8: i32 totalAnswers
}

struct ThriftAnswerDto {
    1: i64 answerId
    2: string employeeEmail
    3: i64 eventId
    4: bool attending
}

exception ThriftInputValidationException {
    1: string message
}

exception ThriftInstanceNotFoundException {
    1: string instanceId
    2: string instanceType
}

exception ThriftAlreadyAnsweredException {
    1: i64 eventId
    2: string employeeEmail
}

exception ThriftAnswerExpiredException {
    1: i64 eventId
}

exception ThriftAnswerCancelledEventException {
    1: i64 eventId
}

exception ThriftAlreadyCanceledException {
    1: i64 eventId
}

exception ThriftEventAlreadyCelebratedException {
    1: i64 eventId
}

service ThriftEventService {

    ThriftEventDto addEvent(1: ThriftEventDto eventDto) throws (1: ThriftInputValidationException e)

    list<ThriftEventDto> findEvents(1: string endDate, 2: string keyword) throws (1: ThriftInputValidationException e)

    ThriftEventDto findEvent(1: i64 eventId) throws (1: ThriftInstanceNotFoundException e)

    ThriftAnswerDto answerEvent(1: i64 eventId, 2: string employeeEmail, 3: bool attending) throws (1: ThriftInputValidationException e, 2: ThriftInstanceNotFoundException ee, 3: ThriftAnswerExpiredException e3, 4: ThriftAlreadyAnsweredException e4, 5: ThriftAnswerCancelledEventException e5)

    void cancelEvent(1: i64 eventId) throws (1: ThriftInstanceNotFoundException e, 2: ThriftAlreadyCanceledException ee, 3: ThriftEventAlreadyCelebratedException eee)

    list<ThriftAnswerDto> findEmployeeAnswers(1: string employeeEmail, 2: bool onlyAffirmative) throws (1: ThriftInputValidationException e)
}