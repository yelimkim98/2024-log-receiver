package naver.kiel0103.logreceiver;

public class CommonUtils {

    private static final String EXTENSION_SEPARATOR = ".";
    private static final String TIME_SEPARATOR = "_";

    /**
     * 파일 이름에 timestamp 를 끼워넣음
     * example
     * @param fileName "파일명1.txt"
     * @return "파일명1_현재시각.txt"
     */
    public static String fileNameCreate(String fileName) {
        int extensionIndex = fileName.lastIndexOf(EXTENSION_SEPARATOR);
        String fileExtension = fileName.substring(extensionIndex + 1);
        String uploadName = fileName.substring(0, extensionIndex);
        String now = String.valueOf(System.currentTimeMillis());

        return uploadName + TIME_SEPARATOR + now + fileExtension;
    }
}
