import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class PolySolver {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java PolySolver <input.json>");
            return;
        }

        String fileName = args[0];
        FileInputStream fis = new FileInputStream(fileName);
        JSONObject root = new JSONObject(new JSONTokener(fis));

        int n = root.getJSONObject("keys").getInt("n");
        int k = root.getJSONObject("keys").getInt("k");

        List<Integer> roots = new ArrayList<>();

        Iterator<String> keys = root.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            if (key.equals("keys")) continue;

            JSONObject node = root.getJSONObject(key);
            int base = Integer.parseInt(node.getString("base"));
            String value = node.getString("value");

            BigInteger decimal = new BigInteger(value, base);
            roots.add(decimal.intValue());
        }

        System.out.println("Decoded roots (decimal): " + roots);
        System.out.println("n = " + n + ", k = " + k);

        List<BigInteger> coeffs = new ArrayList<>();
        coeffs.add(BigInteger.ONE);

        for (int r : roots) {
            coeffs = multiplyByRoot(coeffs, BigInteger.valueOf(r));
        }

        System.out.println("Polynomial coefficients (highest degree first): " + coeffs);
        printPolynomial(coeffs);
    }

    private static List<BigInteger> multiplyByRoot(List<BigInteger> coeffs, BigInteger root) {
        List<BigInteger> newCoeffs = new ArrayList<>();
        newCoeffs.add(coeffs.get(0));

        for (int i = 1; i <= coeffs.size(); i++) {
            BigInteger term1 = (i < coeffs.size()) ? coeffs.get(i) : BigInteger.ZERO;
            BigInteger term2 = coeffs.get(i - 1).negate().multiply(root);
            newCoeffs.add(term1.add(term2));
        }
        return newCoeffs;
    }

    private static void printPolynomial(List<BigInteger> coeffs) {
        StringBuilder sb = new StringBuilder("P(x) = ");
        for (int i = 0; i < coeffs.size(); i++) {
            BigInteger c = coeffs.get(i);
            int degree = coeffs.size() - i - 1;
            if (c.equals(BigInteger.ZERO)) continue;

            if (sb.length() > 6) sb.append(" + ");
            sb.append(c);
            if (degree > 0) sb.append("x");
            if (degree > 1) sb.append("^").append(degree);
        }
        System.out.println(sb);
    }
}
