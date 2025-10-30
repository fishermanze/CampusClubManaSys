com.example.ccms/
├── CcmsApplication.java                 # 主启动类（Spring Boot入口，仅负责启动容器）
├── config/                              # 全局配置层（无业务逻辑，仅配置组件）
│   ├── SecurityConfig.java              # 安全配置（JWT拦截、URL权限控制、角色授权）
│   ├── RedisConfig.java                 # Redis配置（序列化方式、连接池参数）
│   ├── RedissonConfig.java              # Redisson配置（分布式锁初始化）
│   ├── WebConfig.java                   # Web配置（跨域、参数解析器、拦截器注册）
│   └── CurrentUserMethodArgumentResolver.java # @CurrentUser注解解析器（参数绑定）
├── entity/                              # 数据实体层（与数据库表1:1映射，仅含字段+getter/setter）
│   ├── User.java                        # 用户表实体（userId、username、password等核心字段）
│   ├── Club.java                        # 社团表实体（clubId、name、direction等）
│   ├── ClubImage.java                   # 社团图片表实体（关联clubId、图片URL、排序）
│   ├── ClubApplication.java             # 社团申请表实体（关联userId/clubId、申请理由、状态）
│   └── Message.java                     # 消息表实体（关联userId/clubId、内容、类型、已读状态）
├── enums/                               # 枚举常量层（统一状态/类型，避免硬编码）
│   ├── RoleEnum.java                    # 角色枚举（ADMIN=管理员，MANAGER=社团管理，MEMBER=普通成员）
│   ├── RecruitStatusEnum.java           # 招新状态枚举（OPEN=开放，CLOSED=关闭）
│   ├── ApplicationStatusEnum.java       # 申请状态枚举（PENDING=待审核，APPROVED=通过，REJECTED=拒绝）
│   ├── MessageTypeEnum.java             # 消息类型枚举（APPLICATION=申请通知，NOTICE=社团公告，SYSTEM=系统消息）
│   └── ErrorCodeEnum.java               # 错误码枚举（按模块分类：10xxx=用户，20xxx=社团等）
├── repository/                          # 数据访问层（仅数据库操作，无业务逻辑）
│   ├── UserRepository.java              # 用户数据操作（继承JpaRepository，自定义查询方法）
│   ├── ClubRepository.java              # 社团数据操作
│   ├── ClubImageRepository.java         # 社团图片数据操作
│   ├── ClubApplicationRepository.java   # 社团申请数据操作
│   └── MessageRepository.java           # 消息数据操作
├── service/                             # 业务逻辑层（核心业务实现，接口+实现分离）
│   ├── UserService.java                 # 用户业务接口（定义方法规范）
│   ├── ClubService.java                 # 社团业务接口
│   ├── ClubApplicationService.java      # 社团申请业务接口
│   ├── MessageService.java              # 消息业务接口
│   └── impl/                            # 业务实现子包（所有接口的实现类集中存放）
│       ├── UserServiceImpl.java         # 用户业务实现
│       ├── ClubServiceImpl.java         # 社团业务实现
│       ├── ClubApplicationServiceImpl.java # 社团申请业务实现
│       └── MessageServiceImpl.java      # 消息业务实现
├── security/                            # 安全认证层（仅负责认证授权相关）
│   ├── JwtTokenProvider.java            # JWT工具类（生成令牌、解析令牌、验证有效性）
│   ├── JwtAuthenticationFilter.java     # JWT认证过滤器（拦截请求，验证令牌合法性）
│   ├── CustomUserDetailsService.java    # 自定义用户详情服务（从数据库加载用户信息给Spring Security）
│   └── CurrentUser.java                 # 自定义注解（用于Controller层获取当前登录用户ID）
├── controller/                          # 接口控制层（仅接收请求、参数校验、返回响应，无业务逻辑）
│   ├── AuthController.java              # 认证接口（登录、注册，公开访问）
│   ├── UserController.java              # 用户接口（个人信息查询/修改、密码修改，需登录）
│   ├── ClubController.java              # 社团接口（创建、更新、查询，按角色授权）
│   ├── ClubApplicationController.java   # 社团申请接口（申请提交、审批，按角色授权）
│   └── MessageController.java           # 消息接口（消息列表、未读数量、已读标记，需登录）
├── util/                                # 通用工具层（全局复用的工具类，无状态）
│   ├── LockUtil.java                    # 分布式锁工具（Redisson封装，获取/释放锁）
│   ├── ResultUtil.java                  # 响应结果工具（简化ApiResponse创建）
│   ├── ValidationUtil.java              # 参数校验工具（手机号、邮箱、密码格式校验）
│   └── JwtUtil.java                     # JWT辅助工具（可选，与JwtTokenProvider分工）
├── dto/                                 # 数据传输层（前后端数据载体，与entity分离）
│   ├── request/                         # 请求参数DTO（接收前端传入参数，含校验注解）
│   │   ├── LoginRequest.java            # 登录请求（username、password）
│   │   ├── RegisterRequest.java         # 注册请求（username、password、realName等）
│   │   ├── ClubCreateRequest.java       # 社团创建请求（name、direction、maxNumber等）
│   │   ├── ClubUpdateRequest.java       # 社团更新请求（name、introduction等可修改字段）
│   │   ├── ClubApplyRequest.java        # 社团申请请求（clubId、reason）
│   │   └── PasswordChangeRequest.java   # 密码修改请求（oldPassword、newPassword）
│   └── response/                        # 响应结果DTO（返回前端数据，脱敏处理）
│       ├── ApiResponse.java             # 通用响应体（code、message、data）
│       ├── LoginResponse.java           # 登录响应（token、userId、username、role）
│       ├── UserVO.java                  # 用户视图（脱敏，不含password）
│       ├── ClubVO.java                  # 社团视图（含图片URL、当前人数等前端所需信息）
│       ├── ClubApplicationVO.java       # 申请视图（申请状态、处理时间等）
│       └── MessageVO.java               # 消息视图（content、type、isRead等）
├── exception/                           # 异常处理层（统一错误管理）
│   ├── GlobalExceptionHandler.java      # 全局异常处理器（捕获所有异常，返回标准化响应）
│   ├── BusinessException.java           # 业务异常（主动抛出的预期错误）
│   └── SystemException.java             # 系统异常（非预期错误，如数据库连接失败）
└── resources/                           # 配置资源文件（按环境隔离）
    ├── application.yml                  # 主配置文件（公共配置，激活环境）
    ├── application-dev.yml              # 开发环境配置（本地数据库、Redis，日志级别DEBUG）
    ├── application-test.yml             # 测试环境配置（测试服务器数据库，日志级别INFO）
    ├── application-prod.yml             # 生产环境配置（生产数据库、Redis，日志级别WARN，密码环境变量注入）
    └── logback-spring.xml               # 日志配置（输出格式、存储路径、滚动策略）