package xvh;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ProgramConfiguration implements IProgramConfiguration {
    private String input;
    private String filter;
    private String data;
    private String out;

    public ProgramConfiguration(String[] args) {
        //create parsers using reflection
        Set<Consumer<String>> parsers = Arrays.stream(ProgramConfiguration.class.getDeclaredFields())
                .filter(field -> field.getType().isAssignableFrom(String.class))
                .map(field -> {
                    //define a final variable storing a pointer to this object, else it will get encapsulated and the reflection will fail
                    final ProgramConfiguration config = this;
                    //create and return parser
                    return (Consumer<String>) (data) -> {
                        String[] split = data.split("=");
                        if(split.length != 2) {
                            System.out.println("Failed to parse argument: " + data + ", arguments should be specified in the form <arg>=<value>");
                            return;
                        }
                        String arg = split[0].trim();
                        String value = split[1].trim();
                        if (arg.equalsIgnoreCase(field.getName())) {
                            try {
                                field.setAccessible(true);
                                field.set(config, value);
                                System.out.println(" -> Set " + arg + " to " + value);
                            } catch (Exception e) {
                                System.out.println("Failed setting "+ arg + " to " + value);
                                e.printStackTrace();
                            }
                        }
                    };
                }).collect(Collectors.toSet());
        //feed the arguments to the parsers
        Arrays.stream(args).forEach(data -> parsers.forEach(parser -> parser.accept(data)));
    }

    @Override
    public String getInputFile() {
        return this.input;
    }

    @Override
    public String getDataFile() {
        return this.data;
    }

    @Override
    public String getFilter() {
        return this.filter;
    }

    @Override
    public String getOutputFile() {
        return this.out;
    }
}
