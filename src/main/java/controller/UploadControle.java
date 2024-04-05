package controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadControle {
	
	@GetMapping("/")
    public String home() {
        return "upload";
    }
	
	@PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        return "redirect:/sucesso";
    }
	
	@PostMapping("/merge")
    public ResponseEntity<byte[]> handleMergeRequest(@RequestParam("files") MultipartFile[] files) {
        PDFMergerUtility merger = new PDFMergerUtility();
        List<File> tempFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                File tempFile = File.createTempFile("temp", null);
                file.transferTo(tempFile);
                tempFiles.add(tempFile);

                merger.addSource(tempFile);
            } catch (IOException e) {
                for (File tempFile : tempFiles) {
                    tempFile.delete();
                }
                throw new RuntimeException("Error processing file: " + file.getOriginalFilename(), e);
            }
        }

        File outputFile;
        try {
            outputFile = File.createTempFile("merged", ".pdf");
            merger.setDestinationFileName(outputFile.getAbsolutePath());
            merger.mergeDocuments(null);
        } catch (IOException e) {
            for (File tempFile : tempFiles) {
                tempFile.delete();
            }
            throw new RuntimeException("Error merging files", e);
        }

        for (File tempFile : tempFiles) {
            tempFile.delete();
        }

        byte[] fileContent;
        try {
            fileContent = Files.readAllBytes(outputFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException("Error reading merged file", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "merged.pdf");
        headers.setContentLength(fileContent.length);

        return ResponseEntity.ok().headers(headers).body(fileContent);
    }

}
