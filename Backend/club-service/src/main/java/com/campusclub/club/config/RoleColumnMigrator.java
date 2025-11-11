package com.campusclub.club.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * One-time data normalization for club_members.role:
 * - Convert legacy string roles to integer codes
 * - Then ensure column type is INT (MySQL compatible)
 *
 * Codes:
 * 0 = 普通成员, 1 = 负责人, 2 = 管理员
 */
@Component
public class RoleColumnMigrator implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(RoleColumnMigrator.class);
    private final JdbcTemplate jdbcTemplate;

    public RoleColumnMigrator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            log.info("Starting club_members.role column migration...");
            
            // 使用更兼容的方法：先查询所有不同的 role 值，然后逐个处理
            migrateRolesStepByStep();
            
            // 最后尝试修改列类型（如果还没有是 INT 类型）
            try {
                jdbcTemplate.execute("ALTER TABLE club_members MODIFY COLUMN role INT NOT NULL DEFAULT 0");
                log.info("Column type changed to INT successfully");
            } catch (Exception e) {
                log.debug("Column type change skipped (may already be INT): {}", e.getMessage());
            }
            
            log.info("Club members role normalization completed successfully.");
        } catch (Exception e) {
            log.error("Club members role normalization failed: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 分步骤迁移角色值
     * 直接查询所有角色值，在 Java 代码中处理映射，避免 SQL 兼容性问题
     */
    private void migrateRolesStepByStep() {
        try {
            // 查询所有不同的 role 值
            List<Map<String, Object>> allRoles = jdbcTemplate.queryForList(
                "SELECT DISTINCT role FROM club_members"
            );
            
            log.info("Found {} distinct role values to process", allRoles.size());
            
            // 处理每个角色值
            for (Map<String, Object> row : allRoles) {
                Object roleObj = row.get("role");
                if (roleObj == null) {
                    // 处理 NULL 值
                    jdbcTemplate.update("UPDATE club_members SET role = 0 WHERE role IS NULL");
                    log.info("Updated NULL roles to 0");
                    continue;
                }
                
                String roleStr = String.valueOf(roleObj).trim();
                
                // 如果已经是有效的整数（0, 1, 2），跳过
                if (roleStr.equals("0") || roleStr.equals("1") || roleStr.equals("2")) {
                    continue;
                }
                
                // 如果是其他数字，设为 0
                if (roleStr.matches("^[0-9]+$")) {
                    jdbcTemplate.update("UPDATE club_members SET role = 0 WHERE role = ?", roleObj);
                    log.info("Set numeric role '{}' to 0", roleStr);
                    continue;
                }
                
                // 处理字符串角色值
                int mappedRole = mapRoleStringToInt(roleStr);
                jdbcTemplate.update("UPDATE club_members SET role = ? WHERE role = ?", mappedRole, roleObj);
                log.info("Mapped role '{}' to {}", roleStr, mappedRole);
            }
            
        } catch (Exception e) {
            log.error("Error during step-by-step migration: {}", e.getMessage(), e);
            // 不抛出异常，允许应用继续启动，但记录错误
        }
    }
    
    /**
     * 将字符串角色值映射为整数
     * @param roleStr 角色字符串
     * @return 映射后的整数角色值 (0=普通成员, 1=负责人, 2=管理员)
     */
    private int mapRoleStringToInt(String roleStr) {
        if (roleStr == null || roleStr.isEmpty()) {
            return 0;
        }
        
        String upperRole = roleStr.toUpperCase();
        
        // 负责人相关
        if (upperRole.contains("PRESIDENT") || 
            upperRole.contains("LEADER") || 
            upperRole.contains("CHAIRMAN")) {
            return 1;
        }
        
        // 管理员相关
        if (upperRole.contains("ADMIN") || 
            upperRole.contains("MANAGER")) {
            return 2;
        }
        
        // 普通成员或其他
        return 0;
    }
}

