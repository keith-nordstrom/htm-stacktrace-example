package utils;

import org.numenta.nupic.util.Tuple;

import java.util.List;

/**
 * Sugar to convert scala to Java (numenta) Coordinate Tuples more easily
 */
public class TupleConstructor {
    public static Tuple fromSequence(List<Integer> values, Double radius) {
        int[] result = new int[values.size()];
        for (int j = 0; j < values.size(); j++) {
            result[j] = values.get(j);
        }
        return new org.numenta.nupic.util.Tuple(result, radius);
    }
}
