package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.ClientEventService;
import es.udc.ws.app.client.service.dto.ClientAnswerDto;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.exceptions.*;
import es.udc.ws.app.thrift.*;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.time.LocalDateTime;
import java.util.List;

public class ThriftClientEventService implements ClientEventService {

    private final static String ENDPOINT_ADDRESS_PARAMETER =
            "ThriftClientEventService.endpointAddress";

    private final static String endpointAddress =
            ConfigurationParametersManager.getParameter(ENDPOINT_ADDRESS_PARAMETER);

    @Override
    public Long addEvent(ClientEventDto event) throws InputValidationException {

        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try  {
            transport.open();

            return client.addEvent(ClientEventDtoToThriftEventDtoConversor.toThriftEventDto(event)).getEventId();

        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }

    }

    @Override
    public List<ClientEventDto> findEvents(LocalDateTime endDate, String keyword) throws InputValidationException {

        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try  {

            transport.open();

            return ClientEventDtoToThriftEventDtoConversor.toClientEventDtos(client.findEvents(endDate.toString(), keyword));

        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }

    }
    @Override
    public ClientEventDto findEvent(Long eventId) throws InstanceNotFoundException {

        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try  {

            transport.open();

            return ClientEventDtoToThriftEventDtoConversor.toClientEventDto(client.findEvent(eventId));

        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }

    }

    @Override
    public Long answerEvent(Long eventId, String employeeEmail, boolean attending) throws InstanceNotFoundException,
            InputValidationException, ClientAnswerExpiredException, ClientAlreadyAnsweredException, ClientAnswerCancelledEventException {

        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try  {

            transport.open();

            return client.answerEvent(eventId, employeeEmail, attending).getAnswerId();

        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        }catch (ThriftAnswerExpiredException e){
            throw new ClientAnswerExpiredException(e.getEventId());
        } catch (ThriftAlreadyAnsweredException e){
            throw new ClientAlreadyAnsweredException(e.getEventId(), e.getEmployeeEmail());
        } catch (ThriftAnswerCancelledEventException e){
            throw new ClientAnswerCancelledEventException(e.getEventId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }

    }

    @Override
    public void cancelEvent(Long eventId) throws InstanceNotFoundException, ClientAlreadyCanceledException, ClientEventAlreadyCelebratedException {

        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try  {

            transport.open();
            client.cancelEvent(eventId);

        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch (ThriftAlreadyCanceledException e) {
            throw new ClientAlreadyCanceledException(e.getEventId());
        } catch (ThriftEventAlreadyCelebratedException e) {
            throw new ClientEventAlreadyCelebratedException(e.getEventId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }

    }

    @Override
    public List<ClientAnswerDto> findEmployeeAnswers(String employeeEmail, boolean onlyAffirmative) throws InputValidationException {

        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try  {

            transport.open();

            return ClientAnswerDtoToThriftAnswerDtoConversor.toClientAnswerDtos(client.findEmployeeAnswers(employeeEmail, onlyAffirmative));

        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }

    }

    private ThriftEventService.Client getClient() {

        try {

            TTransport transport = new THttpClient(endpointAddress);
            TProtocol protocol = new TBinaryProtocol(transport);

            return new ThriftEventService.Client(protocol);

        } catch (TTransportException e) {
            throw new RuntimeException(e);
        }

    }
}
