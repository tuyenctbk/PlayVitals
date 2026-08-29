# -*- coding: utf-8 -*-
import csv
import io

# Localized store listing data for all 30 supported languages
# Guidelines: App Name <= 30 chars, Short Description <= 80 chars, Full Description <= 4000 chars.
# Highly professional, policy-compliant (no spam, no fake reviews, clean descriptions).

data = {
    "en": {
        "appName": "Game Space Launcher",
        "shortDescription": "Organize games, monitor FPS, RAM, and latency with a real-time floating HUD.",
        "fullDescription": """Game Space Launcher is the ultimate, ads-free companion app designed to optimize and elevate your mobile gaming experience. Gather all your installed games in one organized drawer, track live device telemetry, and train your response speed.

KEY FEATURES:

1. COMPREHENSIVE GAME LIBRARY & ORGANIZER
Auto-detects and aggregates all installed gaming apps into a single, beautifully organized shelf. Say goodbye to app clutter and launch your games directly.

2. LIVE TELEMETRY HUD (FLOATING OVERLAY)
Monitor essential performance metrics in real-time over any game:
• Frames Per Second (FPS)
• Network Latency (Ping)
• Memory Usage (Free RAM)
• Battery Temperature
Customize the overlay opacity, visual style, and eco-refresh rates to fit your taste.

3. ONE-TAP GAME BOOSTER
Optimizes your device memory by cleaning up background services before you start your game. Experience smooth, stutter-free gaming sessions with maximum available RAM.

4. GEMINI AI HARDWARE ADVISOR
Analyze your system specifications (GPU/CPU) via Gemini AI technology to get smart recommendations and custom graphics guide tuning tips.

5. INTERACTIVE REFLEX TRAINER
Improve your split-second reaction times and hand-eye coordination with the built-in precision mini-game, and track your reflex progress.

6. SECURE, PRIVATE & ADS-FREE
An offline-first design ensures your personal gaming telemetry remains on your device. Zero annoying ads, 100% free. Securely sync insights to the cloud via Google Sign-In and Firebase only if you choose to.

Permissions Explained:
• Display Over Other Apps (Overlay): To show the real-time HUD floating panels.
• Query All Packages: To scan and list your installed games.
• Notifications: For background service control and temperature warning alerts."""
    },
    "es": {
        "appName": "Game Space Launcher",
        "shortDescription": "Organiza juegos y monitorea FPS, RAM y latencia con un HUD flotante en vivo.",
        "fullDescription": """Game Space Launcher es el compañero de juegos definitivo y sin anuncios, diseñado para optimizar y elevar tu experiencia de juego móvil. Agrupa todos tus juegos instalados en un cajón organizado, sigue la telemetría del dispositivo en vivo y entrena tu velocidad de reacción.

CARACTERÍSTICAS PRINCIPALES:

1. BIBLIOTECA DE JUEGOS Y ORGANIZADOR COMPLETO
Detecta automáticamente y reúne todas tus aplicaciones de juego instaladas en un solo estante bellamente organizado. Olvídate del desorden de aplicaciones y lanza tus juegos directamente.

2. HUD DE TELEMETRÍA EN VIVO (SUPERPOSICIÓN FLOTANTE)
Monitorea métricas esenciales de rendimiento en tiempo real sobre cualquier juego:
• Fotogramas por segundo (FPS)
• Latencia de red (Ping)
• Uso de memoria (RAM libre)
• Temperatura de la batería
Personaliza la opacidad de la superposición, el estilo visual y las tasas de actualización eco según tu preferencia.

3. OPTIMIZADOR DE JUEGO EN UN TOQUE
Optimiza la memoria de tu dispositivo limpiando los servicios en segundo plano antes de iniciar tu juego. Disfruta de sesiones de juego fluidas y sin tirones con el máximo de RAM disponible.

4. ASESOR DE HARDWARE GEMINI AI
Analiza las especificaciones de tu sistema (GPU/CPU) a través de la tecnología Gemini AI para obtener recomendaciones inteligentes y consejos de ajuste de gráficos personalizados.

5. ENTRENADOR DE REFLEJOS INTERACTIVO
Mejora tus tiempos de reacción rápidos y tu coordinación mano-ojo con el minijuego de precisión integrado, y sigue el progreso de tus reflejos.

6. SEGURO, PRIVADO Y SIN ANUNCIOS
Un diseño centrado en la privacidad fuera de línea garantiza que tu telemetría de juego permanezca en tu dispositivo. Sin anuncios molestos, 100% gratuito. Sincroniza tus datos de forma segura en la nube a través de Google Sign-In y Firebase solo si lo deseas."""
    },
    "vi": {
        "appName": "Game Space Launcher",
        "shortDescription": "Quản lý game và theo dõi FPS, RAM, độ trễ thời gian thực với HUD nổi.",
        "fullDescription": """Game Space Launcher là ứng dụng đồng hành chơi game tối ưu, không quảng cáo, được thiết kế để tối ưu hóa và nâng cao trải nghiệm chơi game trên thiết bị di động của bạn. Tập hợp tất cả các trò chơi đã cài đặt vào một thư viện gọn gàng, theo dõi chỉ số thiết bị trực tiếp và rèn luyện tốc độ phản xạ của bạn.

CÁC TÍNH NĂNG CHÍNH:

1. THƯ VIỆN & QUẢN LÝ TRÒ CHƠI TOÀN DIỆN
Tự động phát hiện và tổng hợp tất cả các ứng dụng trò chơi đã cài đặt vào một kệ game duy nhất được sắp xếp đẹp mắt. Dễ dàng tìm kiếm và khởi chạy trò chơi của bạn ngay lập tức.

2. HUD THEO DÕI TRỰC TIẾP (BẢNG NỔI)
Theo dõi các chỉ số hiệu suất thiết yếu theo thời gian thực phía trên bất kỳ trò chơi nào:
• Số khung hình trên giây (FPS)
• Độ trễ mạng (Ping)
• Sử dụng bộ nhớ (RAM trống)
• Nhiệt độ pin
Tùy chỉnh độ mờ của bảng nổi, phong cách trực quan và giới hạn tần số quét tiết kiệm pin theo sở thích của bạn.

3. TĂNG TỐC GAME 1 CHẠM
Tối ưu hóa bộ nhớ thiết bị của bạn bằng cách dọn dẹp các dịch vụ chạy nền trước khi bạn bắt đầu trò chơi. Trải nghiệm các phiên chơi game mượt mà, không bị giật lag với lượng RAM trống tối đa.

4. TRỢ LÝ PHẦN CỨNG GEMINI AI
Phân tích thông số kỹ thuật hệ thống (GPU/CPU) của bạn thông qua công nghệ Gemini AI để nhận các đề xuất thông minh và mẹo tinh chỉnh đồ họa tùy chỉnh cho từng thiết bị.

5. LUYỆN PHẢN XẠ TƯƠNG TÁC
Cải thiện thời gian phản ứng nhanh và khả năng phối hợp tay mắt của bạn với trò chơi nhỏ luyện phản xạ chính xác được tích hợp sẵn, đồng thời theo dõi tiến trình phản xạ của bạn.

6. AN TOÀN, RIÊNG TƯ & KHÔNG QUẢNG CÁO
Thiết kế ưu tiên ngoại tuyến đảm bảo dữ liệu chơi game cá nhân của bạn vẫn an toàn trên thiết bị của bạn. Không có quảng cáo phiền nhiễu, hoàn toàn miễn phí. Đồng bộ hóa dữ liệu an toàn lên đám mây qua Google Sign-In và Firebase chỉ khi bạn chọn."""
    },
    "fr": {
        "appName": "Game Space Launcher",
        "shortDescription": "Organisez vos jeux et suivez FPS, RAM et latence avec un HUD flottant.",
        "fullDescription": """Game Space Launcher est l'application compagnon ultime et sans publicité conçue pour optimiser et enrichir votre expérience de jeu mobile. Rassemblez tous vos jeux installés dans un tiroir organisé, suivez la télémétrie de l'appareil en temps réel et entraînez votre vitesse de réaction.

PRINCIPALES CARACTÉRISTIQUES :

1. BIBLIOTHÈQUE DE JEUX ET ORGANISATEUR COMPLET
Détecte et regroupe automatiquement tous les jeux installés sur une étagère unique et magnifiquement organisée. Lancez vos jeux préférés directement, sans encombrement.

2. HUD DE TÉLÉMÉTRIE EN DIRECT (SURCOUCHE FLOTTANTE)
Surveillez les indicateurs de performance essentiels en temps réel sur n'importe quel jeu :
• Images par seconde (FPS)
• Latence réseau (Ping)
• Utilisation de la mémoire (RAM libre)
• Température de la batterie
Personnalisez l'opacité, le style visuel et les taux de rafraîchissement éco du HUD selon vos goûts.

3. BOOSTER DE JEU EN UN CLIC
Optimisez la mémoire de votre appareil en nettoyant les processus en arrière-plan avant de lancer votre jeu. Profitez de sessions de jeu fluides, sans saccades, avec le maximum de RAM disponible.

4. CONSEILLER MATÉRIEL GEMINI AI
Analysez les spécifications de votre système (GPU/CPU) via la technologie Gemini AI pour obtenir des recommandations intelligentes et des conseils d'optimisation graphique personnalisés.

5. ENTRAÎNEUR DE RÉFLEXES INTERACTIF
Améliorez vos temps de réaction instantanés et votre coordination œil-main grâce au mini-jeu de précision intégré, et suivez l'évolution de vos scores de réflexes.

6. SÉCURISÉ, PRIVÉ ET SANS PUBLICITÉ
La conception hors ligne garantit que votre télémétrie de jeu reste sur votre appareil. Aucune publicité intrusive, 100% gratuit. Synchronisez vos données en toute sécurité sur le cloud via Google Sign-In et Firebase uniquement si vous le souhaitez."""
    },
    "de": {
        "appName": "Game Space Launcher",
        "shortDescription": "Spiele organisieren und FPS, RAM & Ping mit einem schwebenden HUD überwachen.",
        "fullDescription": """Game Space Launcher ist die ultimative, werbefreie Begleit-App, um Ihr mobiles Gaming-Erlebnis zu optimieren und zu verbessern. Sammeln Sie alle installierten Spiele in einer übersichtlichen Bibliothek, verfolgen Sie die Geräte-Telemetrie live und trainieren Sie Ihre Reaktionsgeschwindigkeit.

HAUPTMERKMALE:

1. SPIELEBIBLIOTHEK & ORGANISATOR
Erkennt installierte Spiele automatisch und fasst sie in einer schön organisierten Ansicht zusammen. Verabschieden Sie sich von App-Chaos und starten Sie Spiele direkt.

2. LIVE-TELEMETRIE-HUD (SCHWEBENDES OVERLAY)
Überwachen Sie wichtige Leistungsdaten in Echtzeit über jedem Spiel:
• Bilder pro Sekunde (FPS)
• Netzwerklatenz (Ping)
• Speichernutzung (Freier RAM)
• Akkutemperatur
Passen Sie die Deckkraft des Overlays, den visuellen Stil und die Eco-Aktualisierungsraten an Ihre Wünsche an.

3. 1-TAP GAME BOOSTER
Optimiert Ihren Gerätespeicher, indem Hintergrunddienste vor dem Start des Spiels bereinigt werden. Erleben Sie flüssige, ruckelfreie Gaming-Sessions mit maximal verfügbarem RAM.

4. GEMINI AI HARDWARE-RATHGEBER
Analysieren Sie Ihre Systemspezifikationen (GPU/CPU) mithilfe der Gemini AI-Technologie, um intelligente Empfehlungen und benutzerdefinierte Tipps zur Grafikanpassung zu erhalten.

5. INTERAKTIVER REFLEXTRAINER
Verbessern Sie Ihre Reaktionszeiten im Bruchteil einer Sekunde und Ihre Hand-Auge-Koordination mit dem integrierten Präzisions-Minispiel und verfolgen Sie Ihre Reflex-Fortschritte.

6. SICHER, PRIVAT & WERBEFREI
Das Offline-First-Design stellt sicher, dass Ihre Gaming-Daten auf Ihrem Gerät bleiben. Keine lästige Werbung, 100% kostenlos. Nutzen Sie die Cloud-Synchronisierung über Google Sign-In und Firebase nur, wenn Sie sich aktiv dafür entscheiden."""
    },
    "pt": {
        "appName": "Game Space Launcher",
        "shortDescription": "Organize jogos e monitore FPS, RAM e ping com um HUD flutuante em tempo real.",
        "fullDescription": """Game Space Launcher é o companheiro de jogos definitivo e livre de anúncios, projetado para otimizar e elevar sua experiência de jogo móvel. Reúna todos os seus jogos instalados em uma gaveta organizada, acompanhe a telemetria do dispositivo em tempo real e treine sua velocidade de reação.

RECURSOS PRINCIPAIS:

1. ORGANIZADOR E BIBLIOTECA DE JOGOS COMPLETA
Detecta automaticamente e agrega todos os jogos instalados em uma única estante lindamente organizada. Diga adeus à desorganização e inicie seus jogos diretamente.

2. HUD DE TELEMETRIA EM TEMPO REAL (PAINEL FLUTUANTE)
Monitore métricas essenciais de desempenho em tempo real acima de qualquer jogo:
• Quadros por segundo (FPS)
• Latência de rede (Ping)
• Uso de memória (RAM livre)
• Temperatura da bateria
Personalize a opacidade do painel flutuante, o estilo visual e as taxas de atualização ecológicas para atender ao seu gosto.

3. OTIMIZADOR DE JOGO COM UM TOQUE
Otimize a memória do seu dispositivo limpando os serviços em segundo plano antes de iniciar o jogo. Desfrute de sessões de jogo fluidas e sem travamentos com o máximo de RAM disponível.

4. CONSULTOR DE HARDWARE GEMINI AI
Analise as especificações do seu sistema (GPU/CPU) por meio da tecnologia Gemini AI para obter recomendações inteligentes e dicas de ajuste de gráficos personalizadas.

5. TREINADOR DE REFLEXOS INTERATIVO
Melhore seus tempos de reação rápidos e sua coordenação motora com o minijogo de precisão integrado, e acompanhe o progresso de seus reflexos.

6. SEGURO, PRIVADO E SEM ANÚNCIOS
O design focado na privacidade offline garante que seus dados de jogo permaneçam no dispositivo. Sem anúncios irritantes, 100% gratuito. Sincronize seus dados com segurança na nuvem via Google Sign-In e Firebase apenas se desejar."""
    },
    "ru": {
        "appName": "Game Space Launcher",
        "shortDescription": "Управляйте играми, следите за FPS, ОЗУ и пингом с помощью плавающего HUD.",
        "fullDescription": """Game Space Launcher — это идеальное приложение-компаньон для геймеров, полностью без рекламы, разработанное для оптимизации и улучшения вашего мобильного игрового процесса. Соберите все установленные игры в одну организованную библиотеку, отслеживайте телеметрию устройства в реальном времени и тренируйте скорость реакции.

КЛЮЧЕВЫЕ ВОЗМОЖНОСТИ:

1. ПОЛНАЯ БИБЛИОТЕКА И УПРАВЛЕНИЕ ИГРАМИ
Автоматически находит и объединяет все установленные игры на одной красивой полке. Забудьте о беспорядке на рабочем столе и запускайте игры мгновенно.

2. ЖИВОЙ HUD ТЕЛЕМЕТРИИ (ПЛАВАЮЩИЙ ОВЕРЛЕЙ)
Контролируйте ключевые показатели производительности прямо во время игры:
• Кадры в секунду (FPS)
• Задержка сети (Пинг)
• Использование памяти (Свободная ОЗУ)
• Температура батареи
Настраивайте прозрачность панелей, визуальный стиль и энергосберегающую частоту обновления.

3. ИГРОВОЙ БУСТЕР В ОДИН КЛИК
Оптимизируйте память вашего устройства, очищая фоновые процессы перед запуском игры. Наслаждайтесь плавным игровым процессом без фризов с максимальным объемом доступной ОЗУ.

4. СОВЕТНИК ПО ЖЕЛЕЗУ GEMINI AI
Проанализируйте характеристики вашего процессора и графического чипа с помощью искусственного интеллекта Gemini AI, чтобы получить умные рекомендации по оптимизации графики.

5. ИНТЕРАКТИВНЫЙ ТРЕНАЖЕР РЕФЛЕКСОВ
Улучшайте скорость реакции и координацию рук и глаз в нашей встроенной мини-игре на точность и следите за динамикой результатов.

6. БЕЗОПАСНО, ПРИВАТНО И БЕЗ РЕКЛАМЫ
Автономная архитектура гарантирует, что ваши данные телеметрии остаются только на вашем устройстве. Никакой назойливой рекламы, 100% бесплатно. Безопасная синхронизация с облаком через Google Вход и Firebase доступна только по вашему желанию."""
    },
    "ja": {
        "appName": "Game Space Launcher",
        "shortDescription": "ゲームを整理し、浮動HUDでFPS、メモリ、応答速度をリアルタイム監視。",
        "fullDescription": """Game Space Launcherは、モバイルゲーム体験を最適化して向上させるために設計された、広告なしの究極のゲームコンパニオンアプリです。インストールされているすべてのゲームを1つの整理された引き出しに集め、リアルタイムのデバイスのテレメトリを追跡し、反応速度をトレーニングします。

主な特徴：

1. 包括的なゲームライブラリ＆オーガナイザー
インストールされているすべてのゲームアプリを自動検出して、美しく整理された単一の棚に集約します。アプリの散らかりに別れを告げ、ゲームを直接起動します。

2. リアルタイム・テレメトリHUD（フローティング・オーバーレイ）
あらゆるゲーム画面上で、重要なパフォーマンス指標をリアルタイムで監視します：
• フレームレート（FPS）
• ネットワーク遅延（Ping）
• メモリ使用量（空きRAM）
• バッテリー温度
オーバーレイの不透明度、ビジュアルスタイル、エコリフレッシュレートを好みに合わせてカスタマイズできます。

3. ワンタップ・ゲームブースター
ゲームを開始する前にバックグラウンドサービスをクリーンアップして、デバイスのメモリを最適化します。最大限に利用可能なRAMを使用して、スムーズでカクつきのないゲームセッションを体験してください。

4. GEMINI AI ハードウェア・アドバイザー
Gemini AIテクノロジーを介してシステム仕様（GPU/CPU）を分析し、スマートな推奨事項やカスタムのグラフィックス調整のヒントを取得します。

5. インタラクティブな反射神経トレーナー
内蔵の精密ミニゲームで、瞬時の反応時間と目と手の協調性を高め、反射神経の進捗を追跡します。

6. 安全、プライベート、そして広告なし
オフライン優先の設計により、個人のゲーム履歴やテレメトリはデバイス上に保持されます。迷惑な広告は一切なく、100%無料です。希望する場合のみ、GoogleログインとFirebaseを介してデータをクラウドに安全に同期できます。"""
    },
    "zh-rCN": {
        "appName": "Game Space Launcher",
        "shortDescription": "整理游戏，并通过实时悬浮 HUD 监测 FPS、运行内存和网络延迟。",
        "fullDescription": """Game Space Launcher 是一款专为优化和提升您的移动游戏体验而设计的无广告游戏伴侣应用。将您安装的所有游戏聚集在一个井井有条的抽屉中，跟踪实时设备遥测数据，并训练您的反应速度。

主要功能：

1. 全面的游戏库和整理器
自动检测并汇总所有已安装的游戏应用到一个设计美观、条理清晰的货架上。告别凌乱的桌面，直接启动您的游戏。

2. 实时性能遥测 HUD（悬浮窗）
在任何游戏画面上实时监控基本性能指标：
• 每秒帧数 (FPS)
• 网络延迟 (Ping)
• 内存使用情况（空闲 RAM）
• 电池温度
根据个人喜好自定义悬浮窗透明度、视觉样式和省电刷新率。

3. 一键游戏加速器
通过在启动游戏前清理后台运行的服务来优化您的设备内存。利用最大可用内存，享受流畅、无卡顿的游戏体验。

4. GEMINI AI 硬件顾问
通过 Gemini AI 技术分析您的系统规格（GPU/CPU），获取智能推荐和定制的图形优化指南。

5. 互动式反射神经训练器
通过内置的精准度迷你游戏，提升您的瞬间反应时间和手眼协调能力，并跟踪您的反应评分进度。

6. 安全、隐私且无广告
离线优先的设计确保您的个人游戏数据安全保留在设备上。零烦人广告，100% 免费。仅在您主动选择时，才通过 Google 登录和 Firebase 安全地将数据同步到云端。"""
    },
    "zh-rTW": {
        "appName": "Game Space Launcher",
        "shortDescription": "整理遊戲，並透過即時懸浮 HUD 監測 FPS、記憶體和網路延遲。",
        "fullDescription": """Game Space Launcher 是一款專為優化和提升您的行動遊戲體驗而設計的無廣告遊戲伴侶應用。將您安裝的所有遊戲聚集在一個井井有條的抽屜中，跟蹤即時設備遙測數據，並訓練您的反應速度。

主要功能：

1. 全面的遊戲庫和整理器
自動檢測並彙整所有已安裝的游戏應用到一個設計美觀、條理清晰的貨架上。告別凌亂的桌面，直接啟動您的遊戲。

2. 即時性能遙測 HUD（懸浮窗）
在任何遊戲畫面上升即時監控基本性能指標：
• 每秒幀數 (FPS)
• 網路延遲 (Ping)
• 記憶體使用情況（空閒 RAM）
• 電池溫度
根據個人喜好自訂懸浮窗透明度、視覺樣式和省電更新率。

3. 一鍵遊戲加速器
透過在啟動遊戲前清理背景運行的服務來優化您的設備記憶體。利用最大可用記憶體，享受流暢、無卡頓的游戏體驗。

4. GEMINI AI 硬件顧問
透過 Gemini AI 技術分析您的系統規格（GPU/CPU），獲取智慧推薦和定制的圖形優化指南。

5. 互動式反射神經訓練器
透過內建的精準度迷你遊戲，提升您的瞬間反應時間和手眼協調能力，並跟蹤您的反應評分進度。

6. 安全、隱私且無廣告
離線優先的设计確保您的個人遊戲數據安全保留在設備上。零煩人廣告，100% 免費。僅在您主動選擇時，才透過 Google 登入和 Firebase 安全地將數據同步到雲端。"""
    },
    "ko": {
        "appName": "Game Space Launcher",
        "shortDescription": "플로팅 HUD로 게임을 정리하고 실시간 FPS, RAM, 핑을 모니터링하세요.",
        "fullDescription": """Game Space Launcher는 모바일 게임 경험을 최적화하고 향상시키기 위해 개발된 광고 없는 최고의 게임 컴패니언 앱입니다. 설치된 모든 게임을 정리된 라이브러리에 모으고, 실시간 기기 원격 측정을 추적하며, 반응 속도를 트레이닝하세요.

주요 특징:

1. 포괄적인 게임 라이브러리 및 정리 도구
설치된 모든 게임 앱을 자동으로 감지하고 아름답게 정렬된 하나의 서랍에 모아줍니다. 앱 혼란에서 벗어나 게임을 바로 실행해 보세요.

2. 실시간 텔레메트리 HUD (플로팅 오버레이)
게임 플레이 중에도 화면 위에 오버레이되어 필수 성능 지표를 실시간으로 보여줍니다:
• 초당 프레임 수 (FPS)
• 네트워크 지연 시간 (Ping)
• 메모리 사용량 (여유 RAM)
• 배터리 온도
오버레이 불투명도, 비주얼 스타일, 에코 리프레시 레이트를 취향에 맞게 사용자 정의해 보세요.

3. 원터치 게임 부스터
게임을 시작하기 전에 백그라운드 서비스를 정리하여 기기 메모리를 최적화합니다. 최대 여유 RAM으로 프레임 드랍 없는 부드러운 게임 플레이를 경험해 보세요.

4. GEMINI AI 하드웨어 어드바이저
Gemini AI 기술을 통해 시스템 사양(GPU/CPU)을 분석하여 스마트한 추천 사양 및 맞춤형 그래픽 가이드 조정 팁을 제공받으세요.

5. 인터랙티브 반응속도 트레이너
내장된 정밀 미니게임을 통해 찰나의 반응 시간과 손과 눈의 협응력을 높이고, 트레이닝 기록을 지속적으로 추적해 보세요.

6. 안전, 프라이빗 및 광고 없음
오프라인 우선 설계로 개인 게임 원격 측정 데이터가 안전하게 기기에 보존됩니다. 성가신 광고가 없으며 100% 무료입니다. 원하는 경우에만 Google 로그인 및 Firebase를 통해 데이터를 클라우드에 안전하게 동기화할 수 있습니다."""
    },
    "it": {
        "appName": "Game Space Launcher",
        "shortDescription": "Organizza giochi e monitora FPS, RAM e ping con un HUD flottante in tempo reale.",
        "fullDescription": """Game Space Launcher è l'applicazione compagna di gioco definitiva e senza pubblicità, progettata per ottimizzare ed elevare la tua esperienza di gioco mobile. Raccogli tutti i tuoi giochi installati in un cassetto organizzato, monitora la telemetria del dispositivo in tempo reale e allena la tua velocità di reazione.

CARATTERISTICHE PRINCIPALI:

1. LIBRERIA DI GIOCHI E ORGANIZZATORE COMPLETO
Rileva automaticamente e aggrega tutte le app di gioco installate in un unico scaffale splendidamente organizzato. Di' addio al disordine delle app e avvia i tuoi giochi direttamente.

2. HUD TELEMETRIA IN TEMPO REAL (SUPERPOSIZIONE FLOTTANTE)
Monitora le metriche di prestazione essenziali in tempo reale sopra qualsiasi gioco:
• Fotogrammi al secondo (FPS)
• Latenza di rete (Ping)
• Utilizzo della memoria (RAM libera)
• Temperatura della batteria
Personalizza l'opacità della superposizione, lo stile visuale e le frequenze di aggiornamento eco in base alle tue preferenze.

3. GAME BOOSTER IN UN TOCCO
Ottimizza la memoria del tuo dispositivo pulendo i servizi in background prima di avviare il gioco. Goditi sessioni di gioco fluide e senza rallentamenti con il massimo della RAM disponibile.

4. CONSULENTE DI HARDWARE GEMINI AI
Analizza le specifiche del tuo sistema (GPU/CPU) tramite la tecnologia Gemini AI per ottenere consigli intelligenti e suggerimenti di sintonizzazione della guida grafica personalizzati.

5. ALLENATORE DI RIFLESSI INTERATTIVO
Migliora i tuoi tempi di reazione rapidi e la coordinazione mano-occhio con il minigioco di precisione integrato, e monitora i tuoi progressi.

6. SICURO, PRIVATO E SENZA PUBBLICITÀ
Il design incentrato sulla privacy offline assicura che i tuoi dati di gioco rimangano sul dispositivo. Nessuna pubblicità fastidiosa, 100% gratuito. Sincronizza i tuoi dati in modo sicuro sul cloud tramite Google Sign-In e Firebase solo se lo desideri."""
    },
    "ar": {
        "appName": "Game Space Launcher",
        "shortDescription": "نظّم ألعابك وراقب معدل الإطارات والرامات والبنغ مع شاشة HUD عائمة.",
        "fullDescription": """برنامج Game Space Launcher هو الرفيق المثالي للألعاب الخالي تمامًا من الإعلانات، والمصمم خصيصًا لتحسين تجربة ألعاب الهاتف المحمول الخاصة بك ورفع مستواها. اجمع كل ألعابك المثبتة في واجهة واحدة منظمة، وراقب القياسات الحيوية للجهاز مباشرة، ودرب سرعة استجابتك.

الميزات الرئيسية:

1. مكتبة ومنظم ألعاب شامل
يكتشف تلقائيًا ويجمع جميع تطبيقات الألعاب المثبتة في رف واحد منظم بشكل جميل. قل وداعًا لفوضى التطبيقات وشغل ألعابك مباشرة.

2. شاشة HUD لمراقبة الأداء في الوقت الفعلي (لوحة عائمة)
راقب مقاييس الأداء الأساسية مباشرة فوق أي لعبة تلعبها:
• عدد الإطارات في الثانية (FPS)
• زمن انتقال الشبكة (البنغ / Ping)
• استخدام الذاكرة (الرام الحرة)
• درجة حرارة البطارية
قم بتخصيص شفافية اللوحة، النمط المرئي، ومعدلات التحديث الاقتصادية لتناسب ذوقك.

3. معزز ألعاب بلمسة واحدة
يقوم بتحسين ذاكرة جهازك عن طريق تنظيف الخدمات التي تعمل في الخلفية قبل بدء اللعبة. اختبر جلسات لعب سلسة وخالية من التقطيع مع أقصى ذاكرة وصول عشوائي متاحة.

4. مستشار أجهزة الذكاء الاصطناعي GEMINI AI
قم بتحليل مواصفات نظامك (معالج الرسوميات/المعالج) عبر تقنية Gemini AI للحصول على توصيات ذكية ونصائح مخصصة لضبط إعدادات الرسوميات.

5. مدرب ردود الفعل التفاعلي
قم بتحسين أوقات رد الفعل في جزء من الثانية والتنسيق بين اليد والعين من خلال اللعبة الصغيرة المدمجة لتدريب ردود الفعل بدقة، وتابع تقدم نتائجك.

6. آمن، خاص، وخالٍ من الإعلانات
يضمن التصميم الذي يعتمد على الأوفلاين أولاً بقاء بيانات ألعابك الحيوية والخاصة بك على جهازك فقط. لا توجد إعلانات مزعجة، مجاني 100%. قم بمزامنة بياناتك بأمان مع السحابة عبر تسجيل الدخول بجوجل وفايربيس فقط إذا اخترت ذلك."""
    },
    "hi": {
        "appName": "Game Space Launcher",
        "shortDescription": "फ्लोटिंग HUD के साथ गेम्स को व्यवस्थित करें, FPS, RAM और पिंग को ट्रैक करें।",
        "fullDescription": """Game Space Launcher अंतिम, विज्ञापन-मुक्त गेमिंग साथी ऐप है जिसे आपके मोबाइल गेमिंग अनुभव को अनुकूलित करने और बेहतर बनाने के लिए डिज़ाइन किया गया है। अपने सभी इंस्टॉल किए गए गेम्स को एक व्यवस्थित स्थान पर लाएं, डिवाइस की स्थिति को लाइव ट्रैक करें, और अपनी प्रतिक्रिया गति का अभ्यास करें।

मुख्य विशेषताएं:

1. व्यापक गेम लाइब्रेरी और ऑर्गनाइज़र
सभी इंस्टॉल किए गए गेमिंग ऐप्स को स्वचालित रूप से पहचानता है और उन्हें एक सुंदर व्यवस्थित शेल्फ पर लाता है। ऐप्स की अव्यवस्था को अलविदा कहें और सीधे गेम लॉन्च करें।

2. लाइव टेलीमेट्री HUD (फ्लोटिंग ओवरले)
किसी भी गेम के चलते रीयल-टाइम में आवश्यक प्रदर्शन मेट्रिक्स की निगरानी करें:
• फ्रेम्स प्रति सेकंड (FPS)
• नेटवर्क विलंबता (Ping)
• मेमोरी उपयोग (फ्री RAM)
• बैटरी का तापमान
अपनी पसंद के अनुसार ओवरले की पारदर्शिता, विज़ुअल स्टाइल और इको-रिफ्रेश दरों को कस्टमाइज़ करें।

3. वन-टैप गेम बूस्टर
आपके गेम शुरू करने से पहले बैकग्राउंड में चल रही सर्विसेज को साफ़ करके आपकी डिवाइस मेमोरी को अनुकूलित करता है। अधिकतम उपलब्ध रैम के साथ बिना किसी रुकावट के सुचारू गेमिंग का अनुभव करें।

4. GEMINI AI हार्डवेयर सलाहकार
स्मार्ट अनुशंसाएं और कस्टम ग्राफिक्स गाइड ट्यूनिंग टिप्स प्राप्त करने के लिए Gemini AI तकनीक के माध्यम से अपने सिस्टम विनिर्देशों (GPU/CPU) का विश्लेषण करें।

5. इंटरैक्टिव रिफ्लेक्स ट्रेनर
इन-बिल्ट सटीक मिनी-गेम के साथ अपनी प्रतिक्रिया समय और हाथ-आंख के समन्वय में सुधार करें, और अपनी रिफ्लेक्स प्रगति को ट्रैक करें।

6. सुरक्षित, निजी और विज्ञापन-मुक्त
ऑफ़लाइन-प्रथम डिज़ाइन यह सुनिश्चित करता है कि आपकी गेमिंग जानकारी आपकी डिवाइस पर सुरक्षित रहे। कोई कष्टप्रद विज्ञापन नहीं, 100% मुफ़्त। यदि आप चाहें तो ही Google साइन-इन और Firebase के माध्यम से क्लाउड पर डेटा सुरक्षित रूप से सिंक करें।"""
    },
    "in": {
        "appName": "Game Space Launcher",
        "shortDescription": "Atur game dan pantau FPS, RAM, serta ping dengan floating HUD waktu nyata.",
        "fullDescription": """Game Space Launcher adalah aplikasi pendamping game terbaik dan bebas iklan yang dirancang untuk mengoptimalkan dan meningkatkan pengalaman bermain game seluler Anda. Kumpulkan semua game terinstal dalam satu wadah yang teratur, lacak telemetri perangkat secara langsung, dan latih kecepatan reaksi Anda.

FITUR UTAMA:

1. PERPUSTAKAAN & PENGATUR GAME YANG LENGKAP
Mendeteksi secara otomatis dan menyatukan semua aplikasi game yang terinstal ke dalam satu rak yang teratur dengan indah. Ucapkan selamat tinggal pada aplikasi yang berantakan dan luncurkan game Anda secara langsung.

2. LIVE TELEMETRY HUD (OVERLAY MELAYANG)
Pantau metrik performa penting secara waktu nyata di atas game apa pun:
• Frame Per Second (FPS)
• Latensi Jaringan (Ping)
• Penggunaan Memori (RAM Bebas)
• Suhu Baterai
Sesuaikan keburaman overlay, gaya visual, dan batas kecepatan refresh ramah lingkungan sesuai selera Anda.

3. PENINGKAT GAME SATU KALI KETUK
Mengoptimalkan memori perangkat Anda dengan membersihkan layanan latar belakang sebelum Anda memulai game. Rasakan sesi game yang lancar dan bebas hambatan dengan RAM maksimum yang tersedia.

4. PENASIHAT PERANGKAT KERAS GEMINI AI
Analisis spesifikasi sistem (GPU/CPU) Anda melalui teknologi Gemini AI untuk mendapatkan rekomendasi cerdas dan tips penyetelan panduan grafis khusus.

5. PELATIH REFLEKS INTERAKTIF
Tingkatkan waktu reaksi cepat dan koordinasi tangan-mata Anda dengan mini-game presisi bawaan, dan lacak kemajuan refleks Anda.

6. AMAN, PRIBADI & BEBAS IKLAN
Desain yang memprioritaskan luring memastikan telemetri game pribadi Anda tetap berada di perangkat Anda. Tanpa iklan yang mengganggu, 100% gratis. Sinkronkan data dengan aman ke cloud melalui Google Sign-In dan Firebase hanya jika Anda memilih untuk melakukannya."""
    },
    "th": {
        "appName": "Game Space Launcher",
        "shortDescription": "จัดการเกมและตรวจสอบ FPS, RAM, และปิงแบบเรียลไทม์ด้วย HUD แบบลอยตัว",
        "fullDescription": """Game Space Launcher เป็นแอปคู่หูการเล่นเกมที่ดีที่สุดและไม่มีโฆษณา ซึ่งได้รับการออกแบบมาเพื่อปรับแต่งและยกระดับประสบการณ์การเล่นเกมบนมือถือของคุณ รวบรวมเกมที่ติดตั้งทั้งหมดไว้ในตู้จัดระเบียบ ติดตามค่าสถิติต่างๆ ของอุปกรณ์แบบสดๆ และฝึกฝนความเร็วในการตอบสนองของคุณ

คุณสมบัติหลัก:

1. คลังและการจัดระเบียบเกมที่ครอบคลุม
ตรวจจับและรวบรวมแอปเกมที่ติดตั้งทั้งหมดเข้าไว้ในชั้นวางที่จัดระเบียบอย่างสวยงามโดยอัตโนมัติ บอกลาความยุ่งเหยิงของแอปและเริ่มเกมของคุณได้โดยตรง

2. HUD แสดงผลสดแบบลอยตัว (FLOATING OVERLAY)
ตรวจสอบเมทริกซ์ประสิทธิภาพที่สำคัญแบบเรียลไทม์บนหน้าจอเกมใดๆ:
• อัตราเฟรมต่อวินาที (FPS)
• ความหน่วงของเครือข่าย (ปิง / Ping)
• การใช้หน่วยความจำ (RAM ที่ว่าง)
• อุณหภูมิแบตเตอรี่
ปรับแต่งความโปร่งแสง รูปแบบภาพ และอัตราการรีเฟรชประหยัดพลังงานตามสไตล์ของคุณ

3. ตัวเร่งเกมด้วยการแตะเพียงครั้งเดียว
ปรับแต่งหน่วยความจำของอุปกรณ์โดยการล้างบริการพื้นหลังก่อนเริ่มเกม สัมผัสประสบการณ์การเล่นเกมที่ราบรื่นและไม่มีสะดุดด้วย RAM ที่ว่างมากที่สุด

4. ที่ปรึกษาฮาร์ดแวร์ GEMINI AI
วิเคราะห์ข้อมูลจำเพาะระบบของคุณ (GPU/CPU) ผ่านเทคโนโลยี Gemini AI เพื่อรับคำแนะนำอัจฉริยะและเคล็ดลับการปรับแต่งคู่มือสถิติกราฟิกที่กำหนดเอง

5. เครื่องฝึกปฏิกิริยาตอบสนองแบบโต้ตอบ
ปรับปรุงเวลาตอบสนองในเสี้ยววินาทีและการประสานงานระหว่างมือและตาด้วยมินิเกมฝึกความแม่นยำในตัว และติดตามความก้าวหน้าของปฏิกิริยาตอบสนองของคุณ

6. ปลอดภัย เป็นส่วนตัว และไม่มีโฆษณา
การออกแบบแบบออฟไลน์เป็นหลักช่วยให้มั่นใจได้ว่าข้อมูลการเล่นเกมส่วนบุคคลของคุณจะอยู่บนอุปกรณ์ของคุณ ไม่มีโฆษณาที่น่ารำคาญ ฟรี 100% ซิงค์ข้อมูลเข้ากับคลาวด์ผ่าน Google Sign-In และ Firebase ได้อย่างปลอดภัยเฉพาะเมื่อคุณเลือกเท่านั้น"""
    },
    "tr": {
        "appName": "Game Space Launcher",
        "shortDescription": "Oyunları düzenleyin; yüzen HUD ile gerçek zamanlı FPS, RAM ve pingi izleyin.",
        "fullDescription": """Game Space Launcher, mobil oyun deneyiminizi optimize etmek ve geliştirmek için tasarlanmış, tamamen reklamsız ve mükemmel bir oyun arkadaşı uygulamasıdır. Yüklü tüm oyunlarınızı düzenli bir çekmecede toplayın, canlı cihaz telemetrisini takip edin ve refleks hızınızı eğitin.

ANA ÖZELLİKLER:

1. KAPSAMLI OYUN KÜTÜPHANESİ VE DÜZENLEYİCİ
Yüklü tüm oyun uygulamalarını otomatik olarak algılar ve güzelce organize edilmiş tek bir rafta toplar. Uygulama karmaşasına son verin ve oyunlarınızı doğrudan başlatın.

2. CANLI TELEMETRİ HUD (YÜZEN PANEL OVERLAY)
Herhangi bir oyunun üzerinde gerçek zamanlı olarak temel performans ölçümlerini izleyin:
• Saniyedeki Kare Sayısı (FPS)
• Ağ Gecikmesi (Ping)
• Bellek Kullanımı (Boş RAM)
• Pil Sıcaklığı
Yüzen panelin opaklığını, görsel stilini ve eko-yenileme hızlarını isteğinize göre özelleştirin.

3. TEK DOKUNUŞLA OYUN HIZLANDIRICI
Oyununuza başlamadan önce arka plan hizmetlerini temizleyerek cihaz belleğinizi optimize eder. Maksimum kullanılabilir RAM ile akıcı ve takılmasız oyun oturumları yaşayın.

4. GEMINI AI DONANIM DANIŞMANI
Gemini AI teknolojisi aracılığıyla sistem özelliklerinizi (GPU/CPU) analiz ederek akıllı öneriler ve özel grafik kılavuzu ayarlama ipuçları alın.

5. ETKİLEŞİMLİ REFLEKS EĞİTİCİSİ
Dahili hassasiyet mini oyunu ile anlık tepki sürelerinizi ve el-göz koordinasyonunuzu geliştirin, refleks gelişim puanlarınızı takip edin.

6. GÜVENLİ, ÖZEL VE REKLAMSIZ
Çevrimdışı öncelikli tasarım, kişisel oyun verilerinizin yalnızca cihazınızda kalmasını sağlar. Can sıkıcı reklamlar yok, %100 ücretsiz. Verilerinizi yalnızca istediğiniz takdirde Google ile Giriş ve Firebase üzerinden güvenle bulutla senkronize edin."""
    },
    "nl": {
        "appName": "Game Space Launcher",
        "shortDescription": "Organiseer games en volg realtime FPS, RAM en ping met een zwevende HUD.",
        "fullDescription": """Game Space Launcher is de ultieme, reclamevrije gaming-metgezel-app die is ontworpen om uw mobiele game-ervaring te optimaliseren en te verbeteren. Verzamel al uw geïnstalleerde games in één georganiseerde bibliotheek, volg live apparaat-telemetrie en train uw reactiesnelheid.

BELANGRIJKSTE KENMERKEN:

1. UITGEBREIDE GAMELIBRARY & ORGANIZER
Detecteert automatisch geïnstalleerde games en voegt ze samen in één overzichtelijke weergave. Zeg vaarwel tegen app-chaos en start uw games rechtstreeks.

2. LIVE TELEMETRIE HUD (ZWEVENDE OVERLAY)
Bewaak essentiële prestatiestatistieken in realtime over elk spel:
• Frames Per Seconde (FPS)
• Netwerklatentie (Ping)
• Geheugengebruik (Vrije RAM)
• Batterijtemperatuur
Pas de transparantie van de overlay, de visuele stijl en eco-verversingsfrequenties naar eigen wens aan.

3. GAME BOOSTER IN ÉÉN TIK
Optimaliseert uw apparaatgeheugen door achtergrondservices op te schonen voordat u uw game start. Ervaar soepele, haperingsvrije gamesessies met maximaal beschikbare RAM.

4. GEMINI AI HARDWARE-ADVISEUR
Analyseer uw systeemspecificaties (GPU/CPU) via Gemini AI-technologie om slimme aanbevelingen en aangepaste tips voor grafische afstemming te krijgen.

5. INTERACTIEVE REFLEXTRAINER
Verbeter uw reactietijden in een fractie van een seconde en uw hand-oogcoördinatie met de ingebouwde precisie-minigame en volg uw reflexvoortgang.

6. VEILIG, PRIVÉ & RECLAMEVRIJ
Het offline-first ontwerp zorgt ervoor dat uw gaming-gegevens op uw apparaat blijven. Geen vervelende advertenties, 100% gratis. Synchroniseer uw gegevens alleen via Google Sign-In en Firebase met de cloud als u dat zelf wilt."""
    },
    "pl": {
        "appName": "Game Space Launcher",
        "shortDescription": "Organizuj gry i monitoruj FPS, RAM oraz ping dzięki pływającemu HUD.",
        "fullDescription": """Game Space Launcher to najlepsza, wolna od reklam aplikacja towarzysząca dla graczy, zaprojektowana w celu optymalizacji i ulepszenia wrażeń z gry na telefonie komórkowym. Zbierz wszystkie zainstalowane gry w jednej zorganizowanej bibliotece, śledź telemetrię urządzenia na żywo i trenuj szybkość reakcji.

KLUCZOWE CECHY:

1. KOMPLEKSOWA BIBLIOTEKA GIER I ORGANIZER
Automatycznie wykrywa i agreguje wszystkie zainstalowane aplikacje gier na jednej pięknie zorganizowanej półce. Zapomnij o bałaganie w aplikacjach i uruchamiaj gry bezpośrednio.

2. HUD TELEMETRII NA ŻYWO (PŁYWAJĄCA NAKŁADKA)
Monitoruj kluczowe wskaźniki wydajności w czasie rzeczywistym podczas każdej gry:
• Klatki na sekundę (FPS)
• Opóźnienie sieci (Ping)
• Zużycie pamięci (Wolna pamięć RAM)
• Temperatura baterii
Dostosuj przezroczystość nakładki, styl wizualny i ekologiczne częstotliwości odświeżania do swoich upodobań.

3. GAME BOOSTER JEDNYM DOTKNIĘCIEM
Optymalizuje pamięć urządzenia poprzez czyszczenie usług w tle przed uruchomieniem gry. Doświadcz płynnych, pozbawionych zacinań sesji gier przy maksymalnej dostępnej pamięci RAM.

4. DORADCA SPRZĘTOWY GEMINI AI
Przeanalizuj specyfikację swojego systemu (GPU/CPU) za pomocą technologii Gemini AI, aby uzyskać inteligentne rekomendacje i niestandardowe wskazówki dotyczące optymalizacji grafiki.

5. INTERAKTYWNY TRENAŻER REFLEKSU
Popraw swój czas reakcji i koordynację wzrokowo-ruchową dzięki wbudowanej precyzyjnej minigrze i śledź postępy swojego refleksu.

6. BEZPIECZNE, PRYWATNE I BEZ REKLAM
Konstrukcja działająca głównie w trybie offline gwarantuje, że Twoje dane telemetryczne pozostaną na Twoim urządzeniu. Zero irytujących reklam, w 100% darmowa. Synchronizuj dane bezpiecznie z chmurą za pomocą logowania Google i Firebase tylko wtedy, gdy chcesz."""
    },
    "uk": {
        "appName": "Game Space Launcher",
        "shortDescription": "Організуйте ігри та відстежуйте FPS, ОЗП й пінг за допомогою плаваючого HUD.",
        "fullDescription": """Game Space Launcher — це найкращий додаток-компаньйон для геймерів без реклами, створений для оптимізації та покращення мобільного ігрового процесу. Зберіть усі встановлені ігри в одній зручній бібліотеці, відстежуйте телеметрію пристрою в реальному часі та тренуйте швидкість реакції.

КЛЮЧОВІ ФУНКЦІЇ:

1. ПОВНА БІБЛІОТЕКА ТА КЕРУВАННЯ ІГРАМИ
Автоматично виявляє та об\'єднує всі встановлені ігри на одній красивій полиці. Забудьте про безлад на робочому столі та запускайте ігри миттєво.

2. ЖИВИЙ HUD ТЕЛЕМЕТРІЇ (ПЛАВАЮЧИЙ ОВЕРЛЕЙ)
Контролюйте ключові показники продуктивності прямо під час гри:
• Кадри в секунду (FPS)
• Затримка мережі (Пинг)
• Використання пам\'яті (Вільна ОЗП)
• Температура батареї
Налаштовуйте прозорість панелей, візуальний стиль та енергозберігаючу частоту оновлення відповідно до своїх уподобань.

3. ІГРОВИЙ БУСТЕР В ОДИН ДОТИК
Оптимізуйте пам\'ять вашого пристрою, очищаючи фонові процеси перед запуском гри. Насолоджуйтеся плавним ігровим процесом без зависань з максимальним обсягом доступної ОЗП.

4. РАДНИК З ЖЕЛЕЗА GEMINI AI
Проаналізуйте характеристики вашого процесора та графічного чіпа за допомогою штучного інтелекту Gemini AI, щоб отримати розумні рекомендації щодо оптимізації графіки.

5. ІНТЕРАКТИВНИЙ ТРЕНАЖЕР РЕФЛЕКСІВ
Покращуйте швидкість реакції та координацію очей і рук у нашій вбудованій міні-грі на точність та відстежуйте динаміку результатів.

6. БЕЗПЕЧНО, ПРИВАТНО ТА БЕЗ РЕКЛАМИ
Автономна архітектура гарантує, що ваші дані телеметрії залишаються тільки на вашому пристрої. Жодної набридливої реклами, 100% безкоштовно. Безпечна синхронізація з хмарою через Google Вхід та Firebase доступна тільки за вашим бажанням."""
    },
    "ro": {
        "appName": "Game Space Launcher",
        "shortDescription": "Organizează jocuri și monitorizează FPS, RAM și latența cu un HUD flotant.",
        "fullDescription": """Game Space Launcher este însoțitorul tău de gaming suprem și fără reclame, conceput pentru a optimiza și îmbunătăți experiența de joc mobil. Reunește toate jocurile tale într-o bibliotecă organizată, monitorizează telemetria dispozitivului în timp real și antrenează-ți viteza de reacție.

CARACTERISTICI PRINCIPALE:

1. BIBLIOTECĂ DE JOCURI ȘI ORGANIZATOR COMPLET
Detectează automat și adună toate aplicațiile de joc instalate într-un singur raft frumos organizat. Spune adio dezordinii și lansează jocurile direct.

2. HUD TELEMETRIE ÎN DIRECT (PANOU FLOTANT)
Monitorizează parametrii esențiali de performanță în timp real deasupra oricărui joc:
• Cadre pe secundă (FPS)
• Latență de rețea (Ping)
• Utilizare memorie (RAM liber)
• Temperatura bateriei
Personalizează opacitatea panoului, stilul vizual și ratele eco de reîmprospătare în funcție de preferințele tale.

3. BOOSTER JOC PRINTR-O ATINGERE
Optimizează memoria dispozitivului prin curățarea serviciilor din fundal înainte de a porni jocul. Experimentează sesiuni de joc fluide, fără sacadări, cu memoria RAM maximă disponibilă.

4. CONSILIER HARDWARE GEMINI AI
Analizează specificațiile sistemului tău (GPU/CPU) cu ajutorul tehnologiei Gemini AI pentru a primi recomandări inteligente și sfaturi grafice personalizate.

5. ANTRENOR DE REFLEXE INTERACTIV
Îmbunătățește-ți timpul de reacție rapid și coordonarea mână-ochi cu mini-jocul de precizie integrat și urmărește evoluția performanțelor tale.

6. SIGUR, PRIVAT ȘI FĂRĂ RECLAME
Designul axat pe confidențialitate offline garantează că telemetria jocului rămâne pe dispozitivul tău. Fără reclame enervante, 100% gratuit. Sincronizează datele în cloud prin Google Sign-In și Firebase doar dacă alegi asta."""
    },
    "hu": {
        "appName": "Game Space Launcher",
        "shortDescription": "Rendszerezze játékait és kövesse az FPS-t, RAM-ot, pinget lebegő HUD-dal.",
        "fullDescription": """A Game Space Launcher a tökéletes, reklámmentes játékostárs alkalmazás, amelyet a mobil játékélmény optimalizálására és növelésére terveztek. Gyűjtse össze az összes telepített játékot egyetlen rendszerezett könyvtárba, kövesse a valós idejű telemetriát, és eddze a reakcióidejét.

FŐBB JELLEMZŐK:

1. JÁTÉKKÖNYVTÁR ÉS RENDSZEREZŐ
Automatikusan felismeri és egyetlen gyönyörű, áttekinthető helyre gyűjti az összes telepített játékalkalmazást. Mondjon búcsút a rendetlenségnek, és indítsa el játékait közvetlenül.

2. VALÓS IDEJŰ TELEMETRIA HUD (LEBEGŐ PANEL)
Kövesse nyomon a kulcsfontosságú teljesítménymutatókat valós időben bármely játék felett:
• Képkockasebesség (FPS)
• Hálózati késleltetés (Ping)
• Memóriahasználat (Szabad RAM)
• Akkumulátor hőmérséklet
Szabja testre az átlátszóságot, a vizuális stílust és a környezetbarát frissítési korlátozásokat az igényeinek megfelelően.

3. EGYÉRINTÉSES JÁTÉKGYORSÍTÓ
Optimalizálja eszköze memóriáját a háttérszolgáltatások leállításával a játék indítása előtt. Tapasztalja meg a zökkenőmentes, akadozásmentes játékmenetet a maximális rendelkezésre álló RAM mellett.

4. GEMINI AI HARDVER TANÁCSADÓ
Elemezze a rendszer specifikációit (GPU/CPU) a Gemini AI technológia segítségével, hogy intelligens ajánlásokat és egyéni grafikai finomhangolási tippeket kapjon.

5. INTERAKTÍV REFLEX EDZŐ
Fejlessze a másodperc törtrésze alatti reakcióidejét és szem-kéz koordinációját a beépített precíziós minijátékkal, és kövesse nyomon a fejlődését.

6. BIZTONSÁGOS, PRIVÁT ÉS REKLÁMMENTES
Az offline-first kialakítás biztosítja, hogy a játéktelemetria az eszközén maradjon. Nincsenek idegesítő hirdetések, 100% ingyenes. Az adatokat biztonságosan szinkronizálhatja a felhőbe a Google Sign-In és a Firebase segítségével, ha ezt választja."""
    },
    "cs": {
        "appName": "Game Space Launcher",
        "shortDescription": "Uspořádejte hry a sledujte FPS, RAM a ping pomocí plovoucího HUD.",
        "fullDescription": """Game Space Launcher je dokonalá aplikace pro hráče bez reklam, navržená tak, aby optimalizovala a pozvedla váš mobilní herní zážitek. Shromážděte všechny nainstalované hry do jedné přehledné knihovny, sledujte telemetrii zařízení v reálném čase a trénujte rychlost reakce.

KLÍČOVÉ VLASTNOSTI:

1. KNIHOVNA HER A USPOŘÁDÁNÍ
Automaticky detekuje a shromažďuje všechny nainstalované herní aplikace na jednu přehlednou poličku. Dejte sbohem chaosu a spouštějte své hry přímo.

2. ŽIVÝ HUD TELEMETRIE (PLOVOUCÍ OVERLAY)
Sledujte klíčové ukazatele výkonu v reálném čase nad jakoukoli hrou:
• Snímky za sekundu (FPS)
• Síťová odezva (Ping)
• Využití paměti (Volná RAM)
• Teplota baterie
Přizpůsobte si průhlednost panelu, vizuální styl a eko-omezovač obnovování podle svého vkusu.

3. OPTIMALIZACE HRY JEDNÍM KLEPNUTÍM
Optimalizuje paměť vašeho zařízení vyčištěním služeb na pozadí před spuštěním hry. Užijte si plynulé herní relace bez sekání s maximální dostupnou pamětí RAM.

4. HARDWAROVÝ PORADCE GEMINI AI
Analyzujte specifikace svého systému (GPU/CPU) pomocí technologie Gemini AI, abyste získali chytrá doporučení a vlastní tipy pro nastavení grafiky.

5. INTERAKTIVNÍ TRENAŽÉR REFLEXŮ
Zlepšete své reakční časy a koordinaci ruka-oko s integrovanou precizní minihrou a sledujte vývoj svých reflexů.

6. BEZPEČNÉ, SOUKROMÉ A BEZ REKLAM
Offline-first design zajišťuje, že vaše herní telemetrie zůstane pouze ve vašem zařízení. Žádné otravné reklamy, 100% zdarma. Synchronizujte data bezpečně do cloudu přes Google přihlášení a Firebase pouze tehdy, když se sami rozhodnete."""
    },
    "el": {
        "appName": "Game Space Launcher",
        "shortDescription": "Organoste ta paichnidia kai deite FPS, RAM kai kathysterisi me floating HUD.",
        "fullDescription": """To Game Space Launcher einai i apolyto synodeftiki efarmogi paichnidion choris diafimiseis, schediasmeni gia na veltistopoiisei kai na anavathmisei tin empeiria sas sta kinita. Sygkentroste ola ta egkatestimena paichnidia se ena organomeno sirtari, parakolouthiste tin tilemetria tis syskevis sas kai ekpaidefste tin tachytita antidrasis sas.

KYRIA CHARAKTERISTIKA:

1. VIVLIOTHIKI KAI DIACHEIRISI PAICHNIDION
Entopizei kai sygkentronei aftomata ola ta egkatestimena paichnidia se ena omorfo kai organomeno sirtari. Ksehaste tin akatastasia kai ksekiniste ta paichnidia sas amesa.

2. LIVE HUD TILEMETRIAS (AIOROUMENO OVERLAY)
Parakolouthiste simantikes metrikis apodosis se real-time pano apo opoiodoipote paichnidi:
• Rythmos ananeosis (FPS)
• Kathysterisi diktyou (Ping)
• Hrisi mnimis (Eleftheri RAM)
• Thermokrasia mpatarias
Prosarmozte tin diafaneia, to optiko styl kai ton eco rythmo ananeosis tou HUD.

3. GAME BOOSTER ME ENA AGGIGMA
Veltistopoiiste tin mnimi tis syskevis sas katharizontas tis paraskinio ypiresies prin ksekinisete to paichnidi sas. Ziste omales synedries paichnidiou choris kollimata me tin megisti diathesimi RAM.

4. SYMVOULOS HARDWARE GEMINI AI
Analyste tis prodiagrafes tou systimatos sas (GPU/CPU) meso tis technologias Gemini AI gia na lavete eksypnes protaseis kai rithmisis gia tin veltisti empeiria grafikon.

5. EKPAIDEFTIS ANAKLASTIKON
Veltionste tous chronous antidrasis kai ton syntonismo heriou-matiou me to ensomatomeno mini-game akriveias kai parakolouthiste tin proodo sas.

6. ASFALES, IDIOTIKO KAI CHORIS DIAFIMISEIS
O offline-first schediasmos diasfalizei oti ta dedomena sas paramenoun sti syskevi sas. Choris enochlitikes diafimiseis, 100% dorean. Synchroniste ta dedomena sas sto cloud me asfaleia meso Google Sign-In kai Firebase mono an to epithymeite."""
    },
    "sv": {
        "appName": "Game Space Launcher",
        "shortDescription": "Organisera spel och övervaka FPS, RAM och latens med en flytande HUD.",
        "fullDescription": """Game Space Launcher är den ultimata, reklamfria appen för spelare, designad för att optimera och höja din mobila spelupplevelse. Samla alla dina installerade spel i ett organiserat bibliotek, följ enhetens telemetri live och träna din reaktionssnabbhet.

HUVUDFUNKTIONER:

1. SPELBIBLIOTEK OCH ORGANISATÖR
Hitta och samla automatiskt alla installerade spel i en vackert organiserad vy. Slipp röran och starta dina spel direkt.

2. LIVE TELEMETRI-HUD (FLYTANDE ÖVERLÄGG)
Övervaka viktiga prestandamått i realtid över vilket spel som helst:
• Bilder per sekund (FPS)
• Nätverkslatens (Ping)
• Minnesanvändning (Ledigt RAM)
• Batteritemperatur
Anpassa opaciteten, den visuella stilen och eco-uppdateringsfrekvenserna för att passa din smak.

3. SPELBOOSTER MED ETT TRYCK
Optimerar enhetens minne genom att rensa bakgrundstjänster innan du startar ditt spel. Upplev smidiga spel utan fördröjningar med maximalt tillgängligt RAM-minne.

4. GEMINI AI MASKINVARRU-RÅDGIVARE
Analysera din enhets specifikationer (GPU/CPU) via Gemini AI-teknik för att få smarta rekommendationer och anpassade tips för grafikinställningar.

5. INTERAKTIV REFLEXTRÄNARE
Förbättra din reaktionssnabbhet och hand-öga-koordination med det inbyggda precision-minispelet, och följ dina framsteg.

6. SÄKER, PRIVAT & REKLAMFRI
En offline-first-design säkerställer att din personliga speldata stannar på din enhet. Inga irriterande annonser, 100 % gratis. Synkronisera data säkert till molnet via Google Sign-In och Firebase endast om du vill."""
    },
    "da": {
        "appName": "Game Space Launcher",
        "shortDescription": "Organiser spil og overvåg FPS, RAM og ping med en flydende HUD.",
        "fullDescription": """Game Space Launcher er den ultimative, reklamefri ledsager-app designet til at optimere og løfte din mobile spiloplevelse. Saml alle dine installerede spil i et organiseret bibliotek, følg enhedstelemetri live, og træn din reaktionshastighed.

NØGLEFUNKTIONER:

1. SPILBIBLIOTEK & ORGANISATOR
Finder og samler automatisk alle installerede spil i en smukt organiseret visning. Slip for rod og start dine spil direkte.

2. LIVE TELEMETRI HUD (FLYTENDE OVERLÆG)
Overvåg vigtige præstationsmålinger i realtid over ethvert spil:
• Billeder pr. sekund (FPS)
• Netværksforsinkelse (Ping)
• Hukommelsesforbrug (Fri RAM)
• Batteritemperatur
Tilpas gennemsigtighed, visuel stil og eco-opdateringshastigheder efter din smag.

3. ET-TRYS GAME BOOSTER
Optimerer din enhedshukommelse ved at rydde baggrundstjenester, før du starter dit spil. Oplev jævne, hakfrie spilsessioner med maksimal tilgængelig RAM.

4. GEMINI AI HARDWARE-RÅDGIVER
Analyser dine systemspecifikationer (GPU/CPU) via Gemini AI-teknologi for at få smarte anbefalinger og tilpassede grafikjusteringstips.

5. INTERAKTIV REFLEKSTRÆNER
Forbedr din reaktionstid og hånd-øje-koordination med det indbyggede præcisions-minispil, og følg dine fremskridt.

6. SIKKER, PRIVAT & REKLAMEFRI
Et offline-first design sikrer, at dine spildata forbliver på din enhed. Ingen irriterende annoncer, 100% gratis. Synkroniser data sikkert til skyen via Google Sign-In og Firebase, hvis du vælger det."""
    },
    "fi": {
        "appName": "Game Space Launcher",
        "shortDescription": "Järjestä pelit ja seuraa FPS:ää, RAM-muistia ja pingiä kelluvalla HUD:lla.",
        "fullDescription": """Game Space Launcher on täydellinen, mainokseton pelikumppanisovellus, joka on suunniteltu optimoimaan ja parantamaan mobiilipelikokemustasi. Kerää kaikki asennetut pelit yhteen järjestettyyn kirjastoon, seuraa laitteen tilaa reaaliajassa ja harjoittele reaktionopeuttasi.

TÄRKEIMMÄT OMINAISUUDET:

1. PELIKIRJASTO JA JÄRJESTELMÄ
Tunnistaa ja kerää automaattisesti kaikki asennetut pelisovellukset yhteen kauniisti järjestettyyn näkymään. Sano hyvästit sovellusten sekasotkulle ja käynnistä pelisi suoraan.

2. REAALIAIKAINEN TELEMETRIA HUD (KELLUVA OVERLAY)
Seuraa tärkeitä suorituskykytietoja reaaliajassa minkä tahansa pelin päällä:
• Ruudunpäivitysnopeus (FPS)
• Verkon viive (Ping)
• Muistin käyttö (Vapaa RAM)
• Akun lämpötila
Mukauta kelluvan paneelin läpinäkyvyyttä, visuaalista tyyliä ja eko-virkistystaajuutta mieltymystesi mukaan.

3. PELIN TEHOSTUS YHDELLÄ NAPAUTUKSELLA
Optimoi laitteesi muistin tyhjentämällä taustapalvelut ennen pelin aloittamista. Koe sujuvat ja nykimättömät pelisessiot maksimaalisella käytettävissä olevalla RAM-muistilla.

4. GEMINI AI -LAITTEISTONEUVOJA
Analysoi järjestelmäsi tekniset tiedot (GPU/CPU) Gemini AI -tekniikan avulla saadaksesi älykkäitä suosituksia ja räätälöityjä vinkkejä grafiikan hienosäätöön.

5. INTERAKTIIVINEN REFLEKSIHARJOITTELIJA
Paranna reaktioaikojasi ja silmä-käsi-koordinaatiotasi sisäänrakennetun tarkkuusminipelin avulla ja seuraa edistymistäsi.

6. TURVALLINEN, YKSITYINEN JA MAINOKSETON
Offline-first-rakenne varmistaa, että pelitietosi pysyvät laitteessasi. Ei häiritseviä mainoksia, 100 % ilmainen. Synkronoi tiedot turvallisesti pilveen Google-kirjautumisen ja Firebasen kautta vain, jos itse haluat."""
    },
    "nb": {
        "appName": "Game Space Launcher",
        "shortDescription": "Organiser spill og overvåk FPS, RAM og ping med et flytende HUD.",
        "fullDescription": """Game Space Launcher er den ultimate, reklamefrie ledsager-appen designet for å optimalisere og løfte din mobile spillopplevelse. Samle alle installerte spill i et organisert bibliotek, følg enhetens telemetri live, og tren reaksjonshastigheten din.

NØKKELFUNKSJONER:

1. SPILLBIBLIOTEK OG ORGANISATOR
Finner og samler automatisk alle installerte spill i en vakkert organisert visning. Slipp app-kaos og start spillene dine direkte.

2. LIVE TELEMETRI HUD (FLYTENDE OVERLEGG)
Overvåk viktige ytelsesmålinger i sanntid over hvilket som helst spill:
• Bilder per sekund (FPS)
• Nettverksforsinkelse (Ping)
• Minnebruk (Ledig RAM)
• Batteritemperatur
Tilpass gjennomsiktighet, visuell stil og eco-oppdateringshastigheter etter eget ønske.

3. ETT-TRYKKS GAME BOOSTER
Optimaliserer enhetens minne ved å rydde bakgrunnstjenester før du starter spillet. Opplev jevne, hakfrie spilløkter med maksimal tilgjengelig RAM.

4. GEMINI AI MASKINVARE-RÅDGIVER
Analyser systemsifikasjonene dine (GPU/CPU) via Gemini AI-teknologi for å få smarte anbefalinger and tilpassede grafikktilpasningstips.

5. INTERAKTIV REFLEKSTRENER
Forbedre reaksjonstiden og hånd-øye-koordinasjonen med det innebygde presisjons-minispillet, og følg fremgangen din.

6. SIKKER, PRIVAT & REKLAMEFRI
Et offline-first design sikrer at dine spilldata forblir på enheten din. Ingen irriterende annonser, 100% gratis. Synkroniser data sikkert til skyen via Google Sign-In og Firebase bare hvis du velger det."""
    },
    "he": {
        "appName": "Game Space Launcher",
        "shortDescription": "ארגן את המשחקים ועקוב אחר FPS, RAM ופינג באמצעות HUD צף בזמן אמת.",
        "fullDescription": """Game Space Launcher הוא אפליקציית העזר האולטימטיבית ללא פרסומות, המיועדת לייעל ולשפר את חוויית המשחקים בנייד שלך. אסוף את כל המשחקים המותקנים שלך במגירה מאורגנת אחת, עקוב אחר מדדי המכשיר בזמן אמת, ואמן את מהירות התגובה שלך.

תכונות עיקריות:

1. ספריית משחקים ומארגן מקיף
מזהה ומקבץ אוטומטית את כל אפליקציות המשחקים המותקנות למדף מאורגן ויפה אחד. תגיד שלום לבלגן והפעל את המשחקים שלך ישירות.

2. HUD טלמטריה בזמן אמת (לוח צף)
עקוב אחר מדדי ביצועים חיוניים בזמן אמת מעל כל משחק:
• קצב פריימים לשנייה (FPS)
• זמני תגובה של הרשת (פינג / Ping)
• שימוש בזיכרון (RAM פנוי)
• טמפרטורת סוללה
התאם אישית את שקיפות הלוח, הסגנון הוויזואלי וקצב רענון החיסכון בהתאם לטעמך.

3. מאיץ משחקים בלחיצה אחת
מייעל את זיכרון המכשיר על ידי ניקוי שירותי רקע לפני תחילת המשחק. חווה מפגשי משחק חלקים ללא השהיות עם מקסימום זיכרון RAM פנוי.

4. יועץ חומרה GEMINI AI
נתח את מפרט המערכת שלך (GPU/CPU) באמצעות טכנולוגיית Gemini AI לקבלת המלצות חכמות וטיפים להתאמה אישית של הגדרות הגרפיקה.

5. מאמן רפלקסים אינטראקטיבי
שפר את זמני התגובה שלך ואת תיאום עין-יד באמצעות מיני-משחק דיוק מובנה, ועקוב אחר התקדמות הרפלקסים שלך לאורך זמן.

6. בטוח, פרטי וללא פרסומות
עיצוב מקומי מקוון-אופליין ראשון מבטיח שנתוני המשחקים שלך יישארו במכשיר שלך בלבד. אפס פרסומות מעצבנות, 100% חינם. סנכרן נתונים בצורה מאובטחת לענן באמצעות כניסת Google ו-Firebase רק אם תבחר בכך."""
    },
    "ms": {
        "appName": "Game Space Launcher",
        "shortDescription": "Urus game dan pantau FPS, RAM serta ping dengan HUD terapung secara langsung.",
        "fullDescription": """Game Space Launcher ialah aplikasi pembantu permainan terbaik dan bebas iklan yang direka untuk mengoptimumkan dan meningkatkan pengalaman permainan mudah alih anda. Kumpulkan semua permainan yang dipasang dalam satu pustaka yang kemas, pantau telemetri peranti secara langsung, dan latih kelajuan refleks anda.

CIRI-CIRI UTAMA:

1. PUSTAKA GAME & PENGATUR YANG LENGKAP
Mengesan secara automatik dan menghimpunkan semua aplikasi permainan yang dipasang ke dalam satu rak yang tersusun indah. Katakan selamat tinggal kepada aplikasi yang berselerak dan lancarkan permainan anda secara terus.

2. LIVE TELEMETRY HUD (OVERLAY TERAPUNG)
Pantau metrik prestasi penting secara langsung di atas sebarang permainan:
• Bingkai Sesaat (FPS)
• Kependaman Rangkaian (Ping)
• Penggunaan Memori (RAM Bebas)
• Suhu Bateri
Sesuaikan kelegapan overlay, gaya visual dan kadar penyegaran eko mengikut citarasa anda.

3. PENGGALAK GAME SATU KETUK
Mengoptimumkan memori peranti anda dengan membersihkan perkhidmatan latar belakang sebelum memulakan permainan anda. Alami sesi permainan yang lancar dan bebas daripada gangguan dengan RAM maksimum yang tersedia.

4. PENASIHAT PERKAKASAN GEMINI AI
Analisis spesifikasi sistem (GPU/CPU) anda melalui teknologi Gemini AI untuk mendapatkan cadangan pintar dan petua pelarasan panduan grafik khusus.

5. LATIHAN REFLEKS INTERAKTIF
Tingkatkan masa tindak balas cepat dan koordinasi tangan-mata anda dengan permainan mini ketepatan terbina dalam, dan jejak kemajuan refleks anda.

6. SELAMAT, PRIVASI & BEBAS IKLAN
Reka bentuk luar talian yang diutamakan memastikan data telemetri permainan peribadi anda kekal pada peranti anda. Tiada iklan yang menjengkelkan, 100% percuma. Segerakkan data dengan selamat ke awan melalui Log Masuk Google dan Firebase hanya jika anda memilih untuk melakukannya."""
    }
}

# Run program verification and export CSV
def generate_csv():
    # Verify constraints
    for lang, values in data.items():
        name_len = len(values["appName"])
        short_len = len(values["shortDescription"])
        full_len = len(values["fullDescription"])
        
        print(f"[{lang}] Verification: Name: {name_len} chars | Short: {short_len} chars | Full: {full_len} chars")
        assert name_len <= 30, f"Language '{lang}' appName exceeded 30 characters!"
        assert short_len <= 80, f"Language '{lang}' shortDescription exceeded 80 characters!"
        assert full_len <= 4000, f"Language '{lang}' fullDescription exceeded 4000 characters!"

    # Create CSV file with UTF-8 encoding
    filepath = "/store_listing_metadata.csv"
    with io.open(filepath, mode="w", encoding="utf-8", newline="") as f:
        writer = csv.writer(f, delimiter=",", quotechar='"', quoting=csv.QUOTE_MINIMAL)
        # Header
        writer.writerow(["Language", "appName", "shortDescription", "fullDescription"])
        
        # Rows
        for lang in sorted(data.keys()):
            values = data[lang]
            writer.writerow([
                lang,
                values["appName"],
                values["shortDescription"],
                values["fullDescription"]
            ])
            
    print(f"\nSUCCESS: CSV generated successfully at: {filepath}")

if __name__ == "__main__":
    generate_csv()
