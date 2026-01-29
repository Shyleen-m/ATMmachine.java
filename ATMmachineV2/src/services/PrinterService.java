package services;

public class PrinterService {
    private int paperLevel;

    public PrinterService(int initialPaper) {
        this.paperLevel = initialPaper;
    }

    public boolean hasPaper() { return paperLevel > 0; }
    public void usePaper() { if (paperLevel > 0) paperLevel--; }
    public int getPaperLevel() { return paperLevel; }
}