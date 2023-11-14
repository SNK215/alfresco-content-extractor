package fr.amexio.extractor;

import fr.amexio.extractor.service.ExtractorService;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new ExtractorService().startExtraction();
    }
}