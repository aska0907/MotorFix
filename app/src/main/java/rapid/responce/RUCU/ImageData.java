package rapid.responce.RUCU;

import java.util.List;

public class ImageData {
    private List<Byte> imageData;

    public ImageData() {
        // Required empty constructor for Firestore
    }

    public ImageData(List<Byte> imageData) {
        this.imageData = imageData;
    }

    public List<Byte> getImageData() {
        return imageData;
    }
}
