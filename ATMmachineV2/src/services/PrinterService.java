package services;

public class PrinterService {
    private int paperLevel;
    private int inkLevel;

    public PrinterService(int paper, int ink) {
        this.paperLevel = paper;
        this.inkLevel = ink;
    }

    // ---------------- GETTERS ----------------
    public int getPaperLevel() { return paperLevel; }
    public int getInkLevel() { return inkLevel; }
    public boolean hasPaper() { return paperLevel > 0; }
    public boolean hasInk() { return inkLevel > 0; }

    // ---------------- USE ----------------
    public void usePaper() { if (paperLevel > 0) paperLevel--; }
    public void useInk() { if (inkLevel > 0) inkLevel--; }

    // ---------------- SETTERS (for technician) ----------------
    public void setPaperLevel(int paperLevel) {
        if (paperLevel < 0) paperLevel = 0;
        this.paperLevel = paperLevel;
    }

    public void setInkLevel(int inkLevel) {
        if (inkLevel < 0) inkLevel = 0;
        this.inkLevel = inkLevel;
    }
}
