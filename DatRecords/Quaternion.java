package DatRecords;
import java.text.DecimalFormat;

/******************************************************************************
 *  Compilation:  javac Quaternion.java
 *  Execution:    java Quaternion
 *
 *  Data type for quaternions.
 *
 *  http://mathworld.wolfram.com/Quaternion.html
 *
 *  The data type is "immutable" so once you create and initialize
 *  a Quaternion, you cannot change it.
 *
 *  % java Quaternion
 *
 ******************************************************************************/

public class Quaternion {
    static DecimalFormat df = new DecimalFormat("0.#############");

    private final double W; //scalar

    private final double X, Y, Z; //vector

    // create a new object with the given components
    public Quaternion(double x0, double x1, double x2, double x3) {
        this.W = x0;
        this.X = x1;
        this.Y = x2;
        this.Z = x3;
    }

    // return a string representation of the invoking object
    public String toString() {
        return df.format(W) + " + " + df.format(X) + "i + " + df.format(Y)
                + "j + " + df.format(Z) + "k";
    }

    // return the quaternion norm
    public double norm() {
        return Math.sqrt(W * W + X * X + Y * Y + Z * Z);
    }

    // return the quaternion conjugate
    public Quaternion conjugate() {
        return new Quaternion(W, -X, -Y, -Z);
    }

    // return a new Quaternion whose value is (this + b)
    public Quaternion plus(Quaternion b) {
        Quaternion a = this;
        return new Quaternion(a.W + b.W, a.X + b.X, a.Y + b.Y, a.Z + b.Z);
    }

    // return a new Quaternion whose value is (this * b)
    public Quaternion times(Quaternion b) {
        Quaternion a = this;
        double y0 = a.W * b.W - a.X * b.X - a.Y * b.Y - a.Z * b.Z;
        double y1 = a.W * b.X + a.X * b.W + a.Y * b.Z - a.Z * b.Y;
        double y2 = a.W * b.Y - a.X * b.Z + a.Y * b.W + a.Z * b.X;
        double y3 = a.W * b.Z + a.X * b.Y - a.Y * b.X + a.Z * b.W;
        return new Quaternion(y0, y1, y2, y3);
    }

    // return a new Quaternion whose value is the inverse of this
    public Quaternion inverse() {
        double d = W * W + X * X + Y * Y + Z * Z;
        return new Quaternion(W / d, -X / d, -Y / d, -Z / d);
    }

    // return a / b
    // we use the definition a * b^-1 (as opposed to b^-1 a)
    public Quaternion divides(Quaternion b) {
        Quaternion a = this;
        return a.times(b.inverse());
    }

    public double[] toEuler() {
        double sqW = W * W;
        double sqX = X * X;
        double sqY = Y * Y;
        double sqZ = Z * Z;
        double yaw = 0.0;
        double pitch = 0.0;
        double roll = 0.0;
        double[] retv = new double[3];
        double unit = sqX + sqY + sqZ + sqW; // if normalised is one, otherwise is correction factor
        double test = W * X + Y * Z;
        if (test > 0.499 * unit) { // singularity at north pole
            yaw = 2 * Math.atan2(Y, W);
            pitch = Math.PI / 2;
            roll = 0;
        } else if (test < -0.499 * unit) { // singularity at south pole
            yaw = -2 * Math.atan2(Y, W);
            pitch = -Math.PI / 2;
            roll = 0;
        } else

            //            const double adbc = q.w*q.z - q.x*q.y;
            //        const double acbd = q.w*q.y - q.x*q.z;
            //        yaw = ::atan2(2*adbc, 1 - 2*(z2+x2));
            //        pitch = ::asin(2*abcd/unitLength);
            //        roll = ::atan2(2*acbd, 1 - 2*(y2+x2));

            yaw = Math.atan2(2.0 * (W * Z - X * Y), 1.0 - 2.0 * (sqZ + sqX));
        pitch = Math.asin(2.0 * test / unit);
        roll = Math.atan2(2.0 * (W * Y - X * Z), 1.0 - 2.0 * (sqY + sqX));
        retv[0] = roll;
        retv[1] = pitch;
        retv[2] = yaw;
        return retv;
    }

    public static Quaternion toQuaternion(double x, double y, double z) {
        x *= 0.5d;
        y *= 0.5d;
        z *= 0.5d;

        double c1 = (double) Math.cos(z);
        double c2 = (double) Math.cos(y);
        double c3 = (double) Math.cos(x);

        double s1 = (double) Math.sin(z);
        double s2 = (double) Math.sin(y);
        double s3 = (double) Math.sin(x);

        Quaternion rtv = new Quaternion(c1 * c2 * c3 - s1 * s2 * s3, c1 * s2
                * c3 - s1 * c2 * s3, s1 * s2 * c3 + c1 * c2 * s3, s1 * c2 * c3
                + c1 * s2 * s3);
        return rtv;
    }

    // sample client for testing
    public static void main(String[] args) {

        //        Quaternion a = new Quaternion(3.0, 1.0, 0.0, 0.0);
        //       System.out.println("a = " + a);
        //        Quaternion b = new Quaternion(0.0, 5.0, 1.0, -2.0);
        //      System.out.println("b = " + b);
        //      System.out.println("norm(a)  = " + a.norm());
        //      System.out.println("conj(a)  = " + a.conjugate());
        //      System.out.println("a + b    = " + a.plus(b));
        //      System.out.println("a * b    = " + a.times(b));
        //      System.out.println("b * a    = " + b.times(a));
        //      System.out.println("a / b    = " + a.divides(b));
        //      System.out.println("a^-1     = " + a.inverse());
        //      System.out.println("a^-1 * a = " + a.inverse().times(a));
        //      System.out.println("a * a^-1 = " + a.times(a.inverse()));

        //row 1127, tickNo 5847

        double w = 0.982691228389739;
        double x = 0.0046360376290977;
        double y = -0.00707337446510791;
        double z = 0.18505784869194;

        double R = Math.toRadians(0.372105030961422);
        double P = Math.toRadians(-0.894867907304848);
        double Y = Math.toRadians(21.3268762696131);

        Quaternion c = new Quaternion(w, x, y, z);
        System.out.println("C = " + c);
        double[] eulerAng = c.toEuler();

        double roll = eulerAng[0];
        double pitch = eulerAng[1];
        double yaw = eulerAng[2];
        System.out.println("Diffs : roll " + (roll - R) + " pitch "
                + (pitch - P) + " Yaw " + (yaw - Y));
        //        Quaternion d = toQuaternion(roll, pitch, yaw);
        //        System.out.println("D = " + d);

        //        Quaternion E = toQuaternion(R, P, Y);
        //
        //        System.out.println("c = " + c);
        //        System.out.println("E = " +E);
        //        
        //        
        //
        //        System.out.println("Euler =" + Math.toDegrees(roll) + " "
        //                + Math.toDegrees(pitch) + " " + Math.toDegrees(yaw));

    }

}
