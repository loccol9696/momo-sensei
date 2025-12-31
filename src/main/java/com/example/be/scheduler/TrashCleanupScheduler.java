package com.example.be.scheduler;

import com.example.be.service.FolderService;
import com.example.be.service.ModuleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TrashCleanupScheduler {

    FolderService folderService;
    ModuleService moduleService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanup() {
        log.info("Bắt đầu tiến trình dọn dẹp thùng rác...");
        moduleService.deleteExpiredModules(LocalDateTime.now().minusDays(30));
        folderService.deleteExpiredFolders(LocalDateTime.now().minusDays(30));
        log.info("Kết thúc tiến trình dọn dẹp.");
    }
}
