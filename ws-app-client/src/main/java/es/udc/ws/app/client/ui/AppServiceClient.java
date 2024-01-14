package es.udc.ws.app.client.ui;

import es.udc.ws.app.client.service.dto.ClientAnswerDto;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import es.udc.ws.app.client.service.*;
import es.udc.ws.app.client.service.dto.ClientEventDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AppServiceClient {
    public static void main(String[] args) {
        if(args.length == 0) {
            printUsageAndExit();
        }
        ClientEventService clientEventService =
                ClientEventServiceFactory.getService();
        if("-a".equals(args[0])) { //AÑADIR EVENTO
            validateArgs(args, 5, new int[] {});

            // [addEvent] EventServiceClient -a <name> <description> <celebrationDate> <duration>
            try {
                Long eventId = clientEventService.addEvent(new ClientEventDto(null,
                        args[1], args[2], LocalDateTime.parse(args[3]), LocalDateTime.parse(args[4])));

                System.out.println("Event " + eventId + " created sucessfully");

            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

        } else if("-F".equals(args[0])) {
            // [findEvents] EventServiceClient -F <endDate> [keyword]
            boolean keyword=false;
            if (args.length==3)
                keyword=true;
            try {
                List<ClientEventDto>  events = clientEventService.findEvents(LocalDateTime.parse(args[1]+"T23:59"), keyword ? args[2]:"");
                System.out.println("Found " + events.size() + " event(s) before end date '" + args[1] + (keyword ? "' and keyword `" + args[2] + "'":"'"));
                for (int i = 0; i < events.size(); i++) {
                    ClientEventDto eventDto = events.get(i);
                    System.out.println("Id: " + eventDto.getEventId() +
                            ", Name: " + eventDto.getName() +
                            ", Description: " + eventDto.getDescription() +
                            ", Celebration date: " + eventDto.getCelebrationDate() +
                            ", End date: " + eventDto.getEndDate() +
                            ", Canceled: " + eventDto.isCanceled() +
                            ", Employees attending: " + eventDto.getEmployeesAttending() +
                            ", Total answers: " + eventDto.getTotalAnswers());
                }

            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

        } else if("-f".equals(args[0])) {
            validateArgs(args, 2, new int[] {1});

            // [findEvent] EventServiceClient -f <eventId>

            try {
                ClientEventDto eventDto = clientEventService.findEvent(Long.parseLong(args[1]));

                System.out.println("Event " + args[1] + " found.");
                System.out.println("Id: " + eventDto.getEventId() +
                        ", Name: " + eventDto.getName() +
                        ", Description: " + eventDto.getDescription() +
                        ", Celebration date: " + eventDto.getCelebrationDate() +
                        ", End date: " + eventDto.getEndDate() +
                        ", Canceled: " + eventDto.isCanceled() +
                        ", Employees attending: " + eventDto.getEmployeesAttending() +
                        ", Total answers: " + eventDto.getTotalAnswers());

            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

        } else if("-aE".equals(args[0])) {
            validateArgs(args, 4, new int[] {1});

            // [answerEvent] EventServiceClient -aE <eventId> <employeeEmail> <attending>

            try {
                Long answerId = clientEventService.answerEvent(Long.parseLong(args[1]), args[2],
                        Boolean.parseBoolean(args[3]));

                System.out.println("Event " + args[1] +
                        " answered successfully with answer number " +
                        answerId);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

        } else if("-c".equals(args[0])) {
            validateArgs(args, 2, new int[] {1});

            // [cancelEvent] EventServiceClient -c <eventId>

            try {
                clientEventService.cancelEvent(Long.parseLong(args[1]));

                System.out.println("Event " + args[1] + " canceled successfully");

            }  catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

        } else if("-FA".equals(args[0])) {
            validateArgs(args, 3, new int[] {});

            // [findEmployeeAnswers] EventServiceClient -FA <employeeEmail> <onlyAffirmative>

            try {
                List<ClientAnswerDto> answers = clientEventService.findEmployeeAnswers(args[1], Boolean.parseBoolean(args[2]));
                System.out.println("Found " + answers.size() + " answers");
                for (int i = 0; i < answers.size(); i++) {
                    ClientAnswerDto answerDto = answers.get(i);
                    System.out.println("Id: " + answerDto.getAnswerId() +
                            ", Employee email: " + answerDto.getEmployeeEmail() +
                            ", Event id: " + answerDto.getEventId() +
                            ", Attending: " + answerDto.isAttending());
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }

    }

    public static void validateArgs(String[] args, int expectedArgs,
                                    int[] numericArguments) {
        if(expectedArgs != args.length) {
            printUsageAndExit();
        }
        for(int i = 0 ; i< numericArguments.length ; i++) {
            int position = numericArguments[i];
            try {
                Double.parseDouble(args[position]);
            } catch(NumberFormatException n) {
                printUsageAndExit();
            }
        }
    }

    public static void printUsageAndExit() {
        printUsage();
        System.exit(-1);
    }

    public static void printUsage() {
        System.err.println("Usage:\n" +
                "    [addEvent] EventServiceClient -a <name> <description> <celebrationDate> <endDate>\n" +
                "    [findEvents] EventServiceClient -F <endDate> [keyword]\n" +
                "    [findEvent] EventServiceClient -f <eventId>\n" +
                "    [answerEvent] EventServiceClient -aE <eventId> <employeeEmail> <attending>\n" +
                "    [cancelEvent] EventServiceClient -c <eventId>\n" +
                "    [findEmployeeAnswers] EventServiceClient -FA <employeeEmail> <onlyAffirmative>\n");


    }
}