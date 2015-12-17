package catchla.yep.model;

public class TaskResponse<D> {
    private final D data;
    private final Throwable exception;

    protected TaskResponse(final D data, final Throwable exception) {
        this.data = data;
        this.exception = exception;
    }

    public D getData() {
        return data;
    }

    public Throwable getException() {
        return exception;
    }

    public static <D> TaskResponse<D> getInstance(final Throwable exception) {
        return new TaskResponse<>(null, exception);
    }

    public static <D> TaskResponse<D> getInstance() {
        return new TaskResponse<>(null, null);
    }

    public static <D> TaskResponse<D> getInstance(final D data) {
        return new TaskResponse<>(data, null);
    }

    public boolean hasData() {
        return data != null;
    }

    public boolean hasException() {
        return exception != null;
    }

    @Override
    public String toString() {
        return "SingleResponse{data=" + data + ", exception=" + exception + "}";
    }
}