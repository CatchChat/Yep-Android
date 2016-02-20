package catchla.yep.model.tuple;

/**
 * Created by mariotaku on 15/6/2.
 */
public class Triple<P1, P2, P3> {

    public P1 p1;
    public P2 p2;
    public P3 p3;

    public Triple(final P1 p1, final P2 p2, final P3 p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public static <P1, P2, P3> Triple<P1, P2, P3> create(P1 p1, P2 p2, P3 p3) {
        return new Triple<>(p1, p2, p3);
    }
}
