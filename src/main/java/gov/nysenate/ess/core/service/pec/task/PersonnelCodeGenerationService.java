package gov.nysenate.ess.core.service.pec.task;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PersonnelCodeGenerationService {

    //FUTURE UPDATES
    //generates a 6 digit code (letters and numbers) and per a submitted task
    //updates the database with the code
    //emails code to relevant people from app props
    //cron to do it every month?

    List<String> numberList = Arrays.asList(IntStream.rangeClosed('0', '9')
            .mapToObj(c -> (char) c+",").collect(Collectors.joining()).split(","));

    List<String> characterList = Arrays.asList(IntStream.concat(IntStream.rangeClosed('P', 'Z'),
                    IntStream.concat( IntStream.rangeClosed('A', 'H'), IntStream.rangeClosed('J', 'N')))
            .mapToObj(c -> (char) c+",").collect(Collectors.joining()).split(","));

    List<String> DecisionList = Arrays.asList(IntStream.rangeClosed('1', '2')
            .mapToObj(c -> (char) c+",").collect(Collectors.joining()).split(","));

    public PersonnelCodeGenerationService() {}

    //Returns a 6 digit code coprised of numbers and letters
    public String createCode() {
        String code = "";
        for (int i=0; i<6; i++) {
            int decision = Integer.parseInt( decideNumberOrCharacter() );
            if (decision == 1) {
                code = code + selectCharacterFromList();
            }
            else {
                code = code + selectNumberFromList();
            }
        }
        return code;
    }

    private String selectNumberFromList() {
        Random rand = new Random();
        return numberList.get(rand.nextInt(numberList.size()));
    }

    private String selectCharacterFromList() {
        Random rand = new Random();
        return characterList.get(rand.nextInt(characterList.size()));
    }

    private String decideNumberOrCharacter() {
        Random rand = new Random();
        return DecisionList.get(rand.nextInt(DecisionList.size()));
    }

}
