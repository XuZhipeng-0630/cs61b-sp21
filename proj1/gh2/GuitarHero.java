package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;
import deque.*;

/**
 * A client that uses the synthesizer package to replicate a plucked guitar string sound
 */
public class GuitarHero {
    public static final String KEYBOARD = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";

    public static void main(String[] args) {

        Deque<GuitarString> guitarStringDeque = new LinkedListDeque<>();
        for (int i = 0; i < KEYBOARD.length(); ++i) {
            double freq = 440 * Math.pow(2, (i - 24) / 12.0);
            guitarStringDeque.addLast(new GuitarString(freq));
        }

        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                for (int i = 0; i < KEYBOARD.length(); ++i) {
                    if (key == KEYBOARD.charAt(i)) {
                        guitarStringDeque.get(i).pluck();
                        break;
                    }
                }
            }

            /* compute the superposition of samples */
            double sample = 0;
            for (int i = 0; i < KEYBOARD.length(); ++i) {
                sample = sample + guitarStringDeque.get(i).sample();
            }
            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (int i = 0; i < KEYBOARD.length(); ++i) {
                guitarStringDeque.get(i).tic();
            }
        }
    }
}

