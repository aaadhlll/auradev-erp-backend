package com.auradev.erp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Resolves a writable directory for tenant logos and CSV imports.
 * Falls back to {@code ./uploads} when a configured path (e.g. {@code /var/data/uploads})
 * is missing or not writable — common on Railway without a mounted volume.
 */
@Slf4j
@Component
public class UploadsDirectoryResolver {

    private final Path uploadsPath;

    public UploadsDirectoryResolver(@Value("${app.uploads.dir:uploads}") String configured) {
        this.uploadsPath = resolveWritable(Path.of(configured));
        log.info("Uploads directory: {}", uploadsPath);
    }

    public Path getPath() {
        return uploadsPath;
    }

    public String getPathString() {
        return uploadsPath.toString();
    }

    private static Path resolveWritable(Path configured) {
        Path absolute = configured.toAbsolutePath().normalize();
        if (isWritable(absolute)) {
            return absolute;
        }

        Path fallback = Path.of("uploads").toAbsolutePath().normalize();
        if (!fallback.equals(absolute) && isWritable(fallback)) {
            log.warn(
                    "Configured uploads dir {} is not writable; using {} instead. "
                            + "For persistent storage on Railway, mount a volume and set APP_UPLOADS_DIR to that mount path.",
                    absolute,
                    fallback);
            return fallback;
        }

        throw new IllegalStateException(
                "No writable uploads directory. Remove APP_UPLOADS_DIR to use ./uploads, "
                        + "or mount a volume at the configured path.");
    }

    private static boolean isWritable(Path dir) {
        try {
            Files.createDirectories(dir);
            Path probe = Files.createTempFile(dir, ".write-probe", ".tmp");
            Files.delete(probe);
            return true;
        } catch (IOException e) {
            log.debug("Uploads path not writable: {} ({})", dir, e.getMessage());
            return false;
        }
    }
}
