package org.example;

import org.apache.chemistry.opencmis.client.api.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Credentials;
import org.example.service.ExtractorService;
import org.example.utils.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {
        ExtractorService extractorService = new ExtractorService();
        extractorService.startExtraction();
    }
}