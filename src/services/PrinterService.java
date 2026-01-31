package services;

public class PrinterService {
    private int paperLevel;
    private int inkLevel;

    public PrinterService(int paper, int ink) { paperLevel = paper; inkLevel = ink; }
    public boolean hasPaper() { return paperLevel > 0; }
    public void usePaper() { if (paperLevel > 0) paperLevel--; }
    public int getPaperLevel() { return paperLevel; }

    public boolean hasInk() { return inkLevel > 0; }
    public void useInk() { if (inkLevel > 0) inkLevel--; }
    public int getInkLevel() { return inkLevel; }
}
