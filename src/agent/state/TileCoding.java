/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.state;

import util.RNG;

/**
 *
 * @author timbrys
 */
public class TileCoding {

    
    int mod(int n, int k) {return (n >= 0) ? n%k : k-1-((-n-1)%k);}

    public static long[] GetTiles(
            int num_tilings, // number of tile indices to be returned in tiles       
            double[] variables, // array of variables
            int memory_size, // total number of possible tiles (memory size)
            int hash1, // change these from -1 to get a different hashing
            int hash2,
            int hash3) {
        long[] tiles = new long[num_tilings];

        int i, j;
        int[] qstate = new int[variables.length];
	int[] base = new int[variables.length];
	int[] coordinates = new int[variables.length + 4];   /* one interval number per rel dimension */
	int num_coordinates;

        if (hash1 == -1) {
            num_coordinates = variables.length + 1;       // no additional hashing corrdinates
        } else if (hash2 == -1) {
            num_coordinates = variables.length + 2;       // one additional hashing coordinates
            coordinates[variables.length + 1] = hash1;
        } else if (hash3 == -1) {
            num_coordinates = variables.length + 3;       // two additional hashing coordinates
            coordinates[variables.length + 1] = hash1;
            coordinates[variables.length + 2] = hash2;
        } else {
            num_coordinates = variables.length + 4;       // three additional hashing coordinates
            coordinates[variables.length + 1] = hash1;
            coordinates[variables.length + 2] = hash2;
            coordinates[variables.length + 3] = hash3;
        }

        /* quantize state to integers (henceforth, tile widths == num_tilings) */
        for (i = 0; i < variables.length; i++) {
            qstate[i] = (int) Math.floor(variables[i] * num_tilings);
            base[i] = 0;
        }

        /*compute the tile numbers */
        for (j = 0; j < num_tilings; j++) {

            /* loop over each relevant dimension */
            for (i = 0; i < variables.length; i++) {

                /* find coordinates of activated tile in tiling space */
                if (qstate[i] >= base[i]) {
                    coordinates[i] = qstate[i] - ((qstate[i] - base[i]) % num_tilings);
                } else {
                    coordinates[i] = qstate[i] + 1 + ((base[i] - qstate[i] - 1) % num_tilings) - num_tilings;
                }

                /* compute displacement of next tiling in quantized space */
                base[i] += 1 + (2 * i);
            }
            /* add additional indices for tiling and hashing_set so they hash differently */
            coordinates[i] = j;
//            System.out.println(Arrays.toString(coordinates));

            tiles[j] = hash_coordinates(coordinates, num_coordinates, memory_size);
        }
        return tiles;
    }

    /* hash_coordinates
     Takes an array of integer coordinates and returns the corresponding tile after hashing 
     */
    static boolean first_call = true;
    static int[] rndseq = new int[2048];
        
    private static int hash_coordinates(int[] coordinates, int num_indices, int memory_size) {
        int i, k;
        long index;
        long sum = 0;
        /* if first call to hashing, initialize table of random numbers */
        if (first_call) {
            for (k = 0; k < 2048; k++) {
                rndseq[k] = RNG.randomInt(Integer.MAX_VALUE);
//                for (i = 0; i < sizeof(int); ++i) {
//                    rndseq[k] = (rndseq[k] << 8) | (rand() & 0xff);
//                }
            }
            first_call = false;
        }
        for (i  = 0; i< num_indices ; i++) {
                    /* add random table offset for this dimension and wrap around */
            index = coordinates[i];
            index += (449 * i);
            index %= 2048;
            while (index < 0) {
                index += 2048;
            }

            /* add selected random number to sum */
            sum += (long) rndseq[(int) index];
        }
        index  = (int) (sum % memory_size);
        while (index< 0){
            index += memory_size ;
        }
        return (int)index;
    }
}
