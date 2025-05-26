# kbHealth

### 1. 기능 요구 사항

#### **인증 및 보안**
* **사용자 로그인**
    - 사용자 계정 정보(아이디, 패스워드)로 로그인할 수 있다.
    - 로그인 성공 시 JWT 토큰을 쿠키로 설정한다.
    - 로그인 실패 시 적절한 오류 메시지를 반환한다.
* **사용자 로그아웃**
    - 현재 세션을 종료하고 JWT 쿠키를 삭제한다.
* **회원가입**
    - 새로운 사용자 계정을 생성할 수 있다.
    - 입력값에 대한 유효성 검증을 수행한다.
* **로그인 상태 확인**
    - 현재 사용자의 로그인 상태와 정보를 확인할 수 있다.
    - 미로그인 시 로그인 필요 예외를 발생시킨다.
* **아이디/닉네임 중복 검사**
    - 회원가입 시 아이디와 닉네임의 중복 여부를 실시간으로 검사한다.
    - 길이, 형식 등의 유효성 검증도 함께 수행한다.

#### **소셜 피드**
* **피드 조회**
    - 사용자의 피드를 페이지네이션으로 조회할 수 있다.
    - 개별 사용자의 피드도 조회할 수 있다.
* **게시글 관리**
    - 텍스트와 이미지를 포함한 새 게시글을 작성할 수 있다.
    - 기존 게시글의 내용을 수정할 수 있다.
    - 게시글을 삭제할 수 있다.
    - 작성자만 수정 및 삭제가 가능하다.
* **댓글 시스템**
    - 특정 게시글의 댓글을 페이지네이션으로 조회할 수 있다.
    - 게시글에 댓글을 작성할 수 있다.
    - 댓글 작성 시 게시글 작성자에게 알림을 생성한다.
    - 댓글을 삭제할 수 있다.
* **좋아요 기능**
    - 게시글에 좋아요를 추가하거나 제거(토글)할 수 있다.
    - 좋아요 시 게시글 작성자에게 알림을 생성한다.
* **게시글 상세 조회**
    - 특정 게시글의 상세 정보를 조회할 수 있다.

#### **알림 시스템**
* **실시간 알림**
    - 댓글, 좋아요, 팔로우 등의 활동에 대한 실시간 알림을 받을 수 있다.
    - WebSocket을 통해 즉시 알림을 전송한다.
* **알림 관리**
    - 사용자의 알림을 페이지네이션으로 조회할 수 있다.
    - 특정 알림을 읽음 상태로 변경할 수 있다.
    - 알림을 삭제할 수 있다.
    - 읽지 않은 알림의 개수를 조회할 수 있다.
    - 여러 알림을 일괄 읽음 처리하거나 모든 알림을 삭제할 수 있다.

#### **사용자 프로필**
* **프로필 조회**
    - 사용자 프로필 정보와 영양 달성도, 최근 10일 점수를 조회할 수 있다.
    - 팔로워 수와 팔로잉 수를 확인할 수 있다.
    - 다른 사용자의 팔로우 여부를 확인할 수 있다.
* **프로필 수정**
    - 프로필 이미지와 기본 정보를 수정할 수 있다.
    - 이미지 업로드를 지원한다.
* **신체 정보 관리**
    - 사용자의 신체 정보(키, 몸무게 등)를 수정할 수 있다.
    - 영양 기준 계산에 활용된다.
* **사용자 검색**
    - 닉네임이나 계정으로 사용자를 검색할 수 있다.

#### **음식 데이터베이스**
* **음식 관리**
    - 기본 음식 데이터는 서버 시작 시 자동으로 로드된다.
* **음식 검색**
    - 음식명으로 음식을 검색할 수 있다.

#### **식단 기록 관리**
* **식단 기록 CRUD**
    - 사용자의 식단 기록 목록을 조회할 수 있다.
    - 음식과 이미지를 포함한 새로운 식단 기록을 생성할 수 있다.
    - 기존 식단 기록을 수정할 수 있다.
    - 식단 기록을 삭제할 수 있다.
* **식단 기록 제약사항**
    - 해당 일자가 지나면 식단 기록은 수정 및 삭제할 수 없다.
    - 하나의 식단 기록에는 하나의 음식만 연결된다.
* **식단 기록 검색**
    - 메뉴 키워드, 시작일, 종료일로 동적 검색할 수 있다.
* **영양소 기준 조회**
    - 사용자의 일일 권장 영양소 정보를 조회할 수 있다.
    - 성별, 몸무게를 기반으로 자동 생성된다.

#### **운동 기록 관리**
* **운동 기록 CRUD**
    - 사용자의 운동 기록 목록을 조회할 수 있다.
    - 운동 종류, 시간, 칼로리와 이미지를 포함한 운동 기록을 생성할 수 있다.
    - 기존 운동 기록을 수정할 수 있다.
    - 운동 기록을 삭제할 수 있다.
* **운동 기록 제약사항**
    - 해당 일자가 지나면 운동 기록은 수정 및 삭제할 수 없다.
* **운동 완료 처리**
    - 운동을 완료 상태로 표시하거나 완료를 해제할 수 있다.
* **운동 기록 검색**
    - 운동명, 시작일, 종료일로 동적 검색할 수 있다.

#### **점수 및 랭킹 시스템**
* **일일 점수 계산**
    - 사용자의 식단 기록과 운동 기록을 통해 일일 점수를 계산할 수 있다.
    - 매일 00시에 전날 기록을 바탕으로 자동 계산된다.
    - **식단 점수 기준**:
        - 칼로리: 권장량 대비 ±10% 이내 → +20점, 초과/미달 → 점수 감소
        - 단백질: 권장량의 90% 이상 → +10점
        - 지방: 권장량 초과 → -5점
        - 당류: 25g 초과 → -5점
        - 나트륨: 2000mg 초과 → -5점
        - 식이섬유: 권장량 이상 → +5점
    - **운동 점수**: 목표 운동량 달성 시 식단 점수 100% 반영, 미달성 시 70% 반영
* **종합 점수 산정**
    - 사용자의 기본 점수는 최근 10일간의 일일 점수에 가중치를 적용해 100점 만점으로 산정된다.
* **랭킹 시스템**
    - 랭킹은 매일 00시 기준으로 자동 갱신된다.
    - 전체 사용자 대상 랭킹을 제공한다.
    - 팔로우한 사용자들만의 랭킹을 제공한다.
    - 동일한 점수의 경우 가입 순서에 따라 순위가 결정된다.

#### **팔로우 시스템**
* **팔로우 관리**
    - 다른 사용자를 팔로우할 수 있다.
    - 팔로우한 사용자를 언팔로우(팔로우 취소)할 수 있다.
    - 팔로우 시 상대방에게 알림을 생성한다.
* **팔로우 제약사항**
    - 이미 팔로우 중인 사용자를 다시 팔로우할 수 없다.
    - 자기 자신을 팔로우하는 것은 불가능하다.
* **팔로우 목록 조회**
    - 특정 사용자의 팔로워 목록을 조회할 수 있다.
    - 특정 사용자의 팔로잉 목록을 조회할 수 있다.

#### **실시간 채팅**
* **채팅방 관리**
    - 사용자 간 1:1 채팅방을 생성할 수 있다.
    - 채팅방 목록을 조회할 수 있다.
* **메시지 송수신**
    - WebSocket을 통해 실시간으로 메시지를 송수신할 수 있다.
    - 채팅 메시지를 페이지네이션으로 조회할 수 있다.
* **읽음 처리**
    - 메시지를 읽음 상태로 처리할 수 있다.
    - 읽지 않은 메시지 총 개수를 조회할 수 있다.
    - 메시지 읽음 상태를 실시간으로 전송한다.

#### **AI 기반 추천**
* **맞춤형 식단 추천**
    - 사용자의 현재 영양 상태를 분석할 수 있다.
    - GPT API를 활용해 개인화된 식단을 추천할 수 있다.
    - 사용자의 성별, 몸무게, 영양 달성도를 고려한 추천을 제공한다.

#### **건강 상태 분석**
* **영양 달성도 분석**
    - 사용자의 일일 영양소 섭취량 대비 권장량 달성률을 계산할 수 있다.
    - 칼로리, 단백질, 지방, 탄수화물, 당류, 나트륨, 식이섬유 등을 종합 분석한다.
* **권장 영양소 관리**
    - 사용자의 성별, 몸무게를 기반으로 개인별 권장 영양소를 자동 생성한다.
    - 하루 권장 칼로리, 단백질, 지방, 탄수화물, 당류, 나트륨, 식이섬유 등을 관리한다.

 ### **예외 처리** 
- 사용자가 잘못된 값을 입력할 경우 적절한 `Exception`을 발생시키고, 오류 메시지를 출력.
- 오류 메시지는 각 예외에 맞는 유형을 처리해야 하며, `IllegalArgumentException`, `IllegalStateException` 등을 사용한다。

### 2. API 명세

### `AuthController` (회원 인증 관련)
| 기능                 | HTTP 메서드 | URI                        |
| -------------------- | ----------- | -------------------------- |
| 로그인               | POST        | `/api/auth/login`          |
| 로그아웃             | GET         | `/api/auth/logout`         |
| 회원가입             | POST        | `/api/auth/regist`         |
| 로그인 상태 확인     | GET         | `/api/auth/check`          |
| 아이디 중복 검사     | GET         | `/api/auth/check-account`  |
| 닉네임 중복 검사     | GET         | `/api/auth/check-username` |

### `FeedController` (소셜 피드 관련)
| 기능                 | HTTP 메서드 | URI                                   |
| -------------------- | ----------- | ------------------------------------- |
| 피드 조회            | GET         | `/api/feed`                           |
| 개별 사용자 피드 조회| GET         | `/api/feed/{member_account}/feed`     |
| 게시글 작성          | POST        | `/api/feed`                           |
| 게시글 수정          | PUT         | `/api/feed/{post_id}`                 |
| 게시글 삭제          | DELETE      | `/api/feed/{post_id}`                 |
| 댓글 목록 조회       | GET         | `/api/feed/{post_id}/commentList`     |
| 댓글 작성            | POST        | `/api/feed/{post_id}/comment`         |
| 댓글 삭제            | DELETE      | `/api/feed/{post_id}/comment/{comment_id}` |
| 좋아요 토글          | PUT         | `/api/feed/{post_id}/like`            |
| 게시글 상세 조회     | GET         | `/api/feed/post/{post_id}`            |

### `NotificationController` (알림 관리)
| 기능                     | HTTP 메서드 | URI                              |
| ------------------------ | ----------- | -------------------------------- |
| 페이징 알림 조회         | GET         | `/api/notifications/paged`       |
| 알림 읽음 처리           | PUT         | `/api/notifications/{notificationId}/read` |
| 알림 삭제                | DELETE      | `/api/notifications/{notificationId}` |
| 전체 알림 조회           | GET         | `/api/notifications`             |
| 읽지 않은 알림 조회      | GET         | `/api/notifications/unread`      |
| 읽지 않은 알림 개수 조회 | GET         | `/api/notifications/unread/count` |
| 여러 알림 읽음 처리      | PUT         | `/api/notifications/read`        |
| 모든 알림 읽음 처리      | PUT         | `/api/notifications/read-all`    |
| 모든 알림 삭제           | DELETE      | `/api/notifications/all`         |

### `ProfileController` (프로필 관리)
| 기능                 | HTTP 메서드 | URI                              |
| -------------------- | ----------- | -------------------------------- |
| 프로필 조회          | GET         | `/api/profile/{member_account}`  |
| 프로필 정보 수정     | POST        | `/api/profile/editinfo`          |
| 신체 정보 수정       | POST        | `/api/profile/editbodyinfo`      |
| 사용자 검색          | GET         | `/api/profile/members/search`    |

### `RecordController` (기록 관련)
| 기능                 | HTTP 메서드 | URI                                     |
| -------------------- | ----------- | --------------------------------------- |
| 식단 목록 조회       | GET         | `/api/records/diet`                     |
| 식단 생성            | POST        | `/api/records/diet`                     |
| 식단 단건 조회       | GET         | `/api/records/diet/{drId}`              |
| 식단 수정            | PUT         | `/api/records/diet/{drId}`              |
| 식단 삭제            | DELETE      | `/api/records/diet/{drId}`              |
| 식단 검색            | GET         | `/api/records/diet/search`              |
| 권장 영양소 조회     | GET         | `/api/records/ns/{member_account}`      |
| 운동 목록 조회       | GET         | `/api/records/exercise`                 |
| 운동 생성            | POST        | `/api/records/exercise`                 |
| 운동 단건 조회       | GET         | `/api/records/exercise/{exId}`          |
| 운동 수정            | PUT         | `/api/records/exercise/{exId}`          |
| 운동 삭제            | DELETE      | `/api/records/exercise/{exId}`          |
| 운동 검색            | GET         | `/api/records/exercise/search`          |
| 운동 완료 처리       | PUT         | `/api/records/exercise/{exId}/complete` |
| 운동 완료 해제       | PUT         | `/api/records/exercise/{exId}/uncomplete` |

### `FollowController` (팔로우/팔로워 관리)
| 기능                 | HTTP 메서드 | URI                                       |
| -------------------- | ----------- | ----------------------------------------- |
| 팔로우               | POST        | `/api/follow/following/{member_id}`       |
| 언팔로우             | DELETE      | `/api/follow/following/{member_id}`       |
| 팔로잉 목록 조회     | GET         | `/api/follow/followingList/{member_id}`   |
| 팔로워 목록 조회     | GET         | `/api/follow/followerList/{member_id}`    |

### `ChatController` (실시간 채팅)
| 기능                     | HTTP 메서드 | URI                                   |
| ------------------------ | ----------- | ------------------------------------- |
| 채팅방 목록 조회         | GET         | `/api/chat/rooms`                     |
| 채팅 메시지 조회         | GET         | `/api/chat/rooms/{chatRoomId}/messages` |
| 메시지 읽음 처리         | POST        | `/api/chat/rooms/{chatRoomId}/read`   |
| 읽지 않은 메시지 개수    | GET         | `/api/chat/unread-count`              |

**WebSocket 엔드포인트:**
| 기능             | 메시지 맵핑      | 설명                    |
| ---------------- | ---------------- | ----------------------- |
| 메시지 전송      | `/app/send-message` | 실시간 채팅 메시지 전송 |

### `GptController` (AI 추천)
| 기능                 | HTTP 메서드 | URI                        |
| -------------------- | ----------- | -------------------------- |
| AI 식단 추천         | POST        | `/api/gpt/recommendDiet`   |

### `ItemController` (음식 데이터베이스)
| 기능                 | HTTP 메서드 | URI                        |
| -------------------- | ----------- | -------------------------- |
| 음식 목록 조회       | GET         | `/api/items`               |
| 음식 추가            | POST        | `/api/items`               |
| 특정 음식 조회       | GET         | `/api/items/{dietId}`      |
| 음식 수정            | PUT         | `/api/items/{dietId}`      |
| 음식 삭제            | DELETE      | `/api/items/{dietId}`      |
| 음식 검색            | GET         | `/api/items/search`        |
| 음식 데이터 로드     | GET         | `/api/items/load-data`     |

### `RankingController` (랭킹 시스템)
| 기능                 | HTTP 메서드 | URI                        |
| -------------------- | ----------- | -------------------------- |
| 전체 랭킹 조회       | GET         | `/api/ranking`             |
| 팔로우 랭킹 조회     | GET         | `/api/ranking/following`   |

### WebSocket 통신 엔드포인트

**연결 설정:**
- **WebSocket URL**: `/ws`
- **STOMP Prefix**: `/app` (클라이언트 → 서버)
- **Broker Prefix**: `/topic`, `/queue` (서버 → 클라이언트)

**개인별 구독 경로:**
| 구독 경로                              | 설명                    |
| -------------------------------------- | ----------------------- |
| `/user/queue/notifications`            | 개별 알림 수신          |
| `/user/queue/notification-count`       | 안읽은 알림 개수        |
| `/user/queue/notification-list-update` | 알림 목록 업데이트      |
| `/user/queue/chat-messages`            | 채팅 메시지 수신        |
| `/user/queue/chat-room-update`         | 채팅방 정보 업데이트    |
| `/user/queue/chat-unread-count`        | 안읽은 채팅 개수        |
| `/user/queue/message-read-status`      | 메시지 읽음 상태        |

**전역 구독 경로:**
| 구독 경로                              | 설명                    |
| -------------------------------------- | ----------------------- |
| `/topic/global-notifications`          | 전체 사용자 공지        |
| `/topic/group/{groupId}`               | 그룹별 알림             |

### 3. 배포 및 인프라

#### **서비스 URL**
- **메인 서비스**: http://healthcomfit.site
- **API 서버**: http://healthcomfit.site/api

#### **AWS EC2 환경**
- **인스턴스 타입**: t3.medium
- **운영체제**: Amazon Linux 2023
- **Java 런타임**: OpenJDK 21
- **데이터베이스**: MySQL 8.0
- **웹 서버**: Nginx (리버스 프록시)

#### **배포 구성**
```
EC2 Instance (t3.medium)
├── Nginx (포트 80/443) → Spring Boot (포트 8080)
├── MySQL 8.0 (포트 3306)
├── SSL 인증서 (Let's Encrypt)
├── 정적 파일 서빙 (/images)
└── WebSocket 프록시 설정
```

#### **보안 설정**
- **방화벽**: Amazon Linux firewalld 활성화
- **포트 제한**: 22(SSH), 80(HTTP), 443(HTTPS), 3306(MySQL)만 개방
- **JWT 토큰**: HttpOnly 쿠키로 XSS 방지
- **CORS**: 프론트엔드 도메인만 허용
- **MySQL**: 로컬 접속만 허용, 강력한 패스워드 설정

#### **성능 최적화**
- **Nginx 압축**: gzip 활성화
- **정적 파일 캐싱**: 이미지 파일 브라우저 캐싱
- **MySQL Connection Pooling**: HikariCP 연결 풀 설정
- **JVM 옵션**: Java 21 최적화 및 메모리 설정
- **t3.medium 성능**: 2 vCPU, 4GB RAM 활용

### 4. 수정사항

* 테스트케이스
    - FollowTest: 팔로우 기능 테스트
    - MemberTest: 회원 기능 테스트
    - DietTest: 음식과 식단기록 기능 테스트
    - ExerciseTest: 운동 기록 기능 테스트
