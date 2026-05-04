package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;
import deque.*;

/**
 * A client that uses the synthesizer package to replicate a plucked guitar string sound
 */
public class GuitarHero {
    public static final String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    public static Deque<GuitarString> GuitarStringDeque;

    public static void main(String[] args) {

        GuitarStringDeque = new LinkedListDeque<>();
        for(int i = 0; i < keyboard.length(); ++i) {
            double freq = 440 * Math.pow(2, (i - 24) / 12.0);
            GuitarStringDeque.addLast(new GuitarString(freq));
        }

        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                for (int i = 0; i < keyboard.length(); ++i) {
                    if (key == keyboard.charAt(i)) {
                        GuitarStringDeque.get(i).pluck();
                        break;
                    }
                }
            }

            /* compute the superposition of samples */
            double sample = 0;
            for (int i = 0; i < keyboard.length(); ++i) {
                sample = sample + GuitarStringDeque.get(i).sample();
            }
            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (int i = 0; i < keyboard.length(); ++i) {
                GuitarStringDeque.get(i).tic();
            }
        }
    }
}

