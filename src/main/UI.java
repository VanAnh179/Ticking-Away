package main;

import entity.Entity;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;

public class UI {
    //private MainFrame mainFrame;
    GamePanel gp;
    BufferedImage heart, keyImage, heart2;
    public boolean messageOn = false;
    public String message = "";
    int messageCounter = 0;
    public boolean gameFinished = false;
    public boolean gameWon = false; // Thêm biến gameWon
    protected Sound textSound = new Sound();
    protected Sound tutorialSound = new Sound();

    long startTime;
    private long lastBackgroundScoreUpdate;
    private boolean isRunning;
    
    public int visibleScore = 0;
    public int backgroundScore = 50000;
    public final int BG_SCORE_DECREASE = 13;

    // Thêm các biến cho tutorial
    public boolean showTutorial = true;
    private String[] tutorialPages = {
        "Tôi đang ở đâu thế này?\n...",
        "...",
        "Vừa tỉnh dậy ở một nơi xa lạ, xung quanh chỉ toàn là hư vô.\nTrước mắt tôi là một tòa lâu đài u ám.\nKhông còn cách nào khác...\ntôi buộc phải tiến vào.",
        "Và giờ, tôi đứng đây... giữa đống đổ nát hoang tàn.",
        "Ự...\n\nAAAA!",
        "[SHOW_IMAGE portal_vision 2000]", // Placeholder để chèn ảnh
        "Đau đầu quá...",
        "Tôi... \ntôi phải tìm cách thoát khỏi đây ngay...\nMọi thứ đang sụp đổ, hoặc... chính tôi đang mất trí.",
        "Hy vọng ngọn đuốc này có thể cháy đủ lâu...\nCho đến khi tôi tìm được cánh cửa đó.",
        "Cơn đau đầu vừa rồi khiến tôi thấy một ảo ảnh...\nhình ảnh về một cánh cửa...",
        "Chắc chắn đó là lối ra...\nVà thứ tôi cần bây giờ là...\nchìa khóa.",
        "Được rồi.\nMình có khả năng tạo ra bom bằng cách\nkết hợp các hạt nguyên tử trong không khí.",
        "Hãy thử với viên đá kia xem!"
    };
    private int currentTutorialPage = 0;
    private int currentCharIndex = 0;
    private long lastCharTime;
    private final int CHAR_DELAY = 50; // Thời gian hiện từng chữ (ms)
    private final Color BORDER_COLOR = new Color(255, 105, 180); // Màu viền hồng

    public boolean hasShownKeyMessage = false;
    public int keyCharIndex = 0;
    public long keyLastCharTime;
    public boolean showKeySequence = false;
    public String[] keyMessages = {
        "A! Chìa khóa đây rồi!",
        "[SHOW_IMAGE key_01d 2000]", // Hiệu ứng ảnh key
        "Tuyệt quá!\nGiờ mình có thể thoát khỏi đây rồi!",
        "Giờ tìm cánh cửa đó thôi nào"
    };
    private int currentKeyPage = 0;

    public boolean showPortalSequence = false;
    public String[] portalMessages = {
        "Ô,...",
        "...",
        "Cánh cửa này cần tận 3 chìa khóa sao?",
        "Phải tìm thêm chìa khóa mới được."
    };
    private int currentPortalPage = 0;

    private Rectangle okButtonRect;

    public BufferedNameEffect currentEffect;
    public long effectStartTime;
    public boolean showEffect = false;
    private HashMap<String, BufferedImage> effectImages = new HashMap<>();

    private Rectangle menuButtonRect;
    private final Color MENU_BUTTON_COLOR = new Color(255, 105, 180); // Màu hồng
    private final Color MENU_BUTTON_TEXT_COLOR = Color.WHITE;


    public UI(GamePanel gp) {
        this.gp = gp;
        textSound.setFile(11);
        tutorialSound.setFile(12);
        effectImages = new HashMap<>(); // Khởi tạo map
        try {
            // Thêm load ảnh
            BufferedImage portalImage = ImageIO.read(getClass().getResourceAsStream("/background/portal_vision.png"));
            BufferedImage keyEffectImage = ImageIO.read(getClass().getResourceAsStream("/objects/key_01d.png"));
            keyEffectImage = scaleImage(keyEffectImage, 128, 128);
            effectImages.put("key_01d", keyEffectImage);
            if (portalImage != null) {
                portalImage = scaleImage(portalImage, 256, 256);
                effectImages.put("portal_vision", portalImage);
                System.out.println("Đã tải thành công ảnh portal_vision");
            } else {
                System.out.println("Không tìm thấy file ảnh portal_vision");
            }
            heart = ImageIO.read(getClass().getResourceAsStream("/objects/health.png"));
            heart2 = ImageIO.read(getClass().getResourceAsStream("/objects/heart2.png"));
            keyImage = ImageIO.read(getClass().getResourceAsStream("/objects/key_01d.png")); // Load ảnh key
            keyImage = scaleImage(keyImage, 30, 30);
            heart2 = scaleImage(heart, 30, 30);
        } catch (IOException e) {
            System.err.println("Lỗi tải ảnh:");
            e.printStackTrace();
        }
        resetTimer();

        System.out.println("Danh sách key trong effectImages:");
        for (String key : effectImages.keySet()) {
            System.out.println("[" + key + "]");
        }
    }
    
    private BufferedImage scaleImage(BufferedImage original, int width, int height) {
        BufferedImage scaledImage = new BufferedImage(width, height, original.getType());
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.drawImage(original, 0, 0, width, height, null);
        g2d.dispose();
        return scaledImage;
    }

    public void resetTimer() {
        isRunning = false;
        visibleScore = 0;
        backgroundScore = 50000;
    }

    public void startTimer() {
        if(!isRunning || !showTutorial || !gp.isStartingEffect) {
            startTime = System.currentTimeMillis();
            lastBackgroundScoreUpdate = startTime;
            isRunning = true;
        }
    }

    public void update() {
        if (!gp.isPaused && isRunning && !showTutorial && !showKeySequence && !gp.isStartingEffect) {
            long currentTime = System.currentTimeMillis() - startTime - gp.totalPausedTime;
            if (currentTime - lastBackgroundScoreUpdate >= 1000) {
                backgroundScore -= BG_SCORE_DECREASE;
                if (backgroundScore < 0) backgroundScore = 0;
                lastBackgroundScoreUpdate = currentTime;
            }
        }
        if (showEffect && System.currentTimeMillis() - effectStartTime > currentEffect.duration) {
            System.out.println("Effect finished. Moving to next page.");
            showEffect = false;
            if (currentTutorialPage < tutorialPages.length - 1) {
                currentTutorialPage++; // Chuyển trang
                currentCharIndex = 0; // Reset chỉ số ký tự
                // Đảm bảo không kích hoạt lại hiệu ứng khi đã chuyển trang
                showEffect = false;
                System.out.println("Current page: " + currentTutorialPage);
            }
        }
    }

    public void addScoreForRock(int points) {
        visibleScore += points;
    }

    public void addScore(Entity enemy) {
        gp.addScoreForEnemy(enemy);
    }

    public int getFinalScore() {
        if (gameFinished && !gameWon) {
            return visibleScore;
        }
        return visibleScore + backgroundScore;
    }

    private String getFormattedTime() {
        if (gp == null || !isRunning) return "00:00";
        long currentTime = System.currentTimeMillis() - startTime - gp.totalPausedTime;
        currentTime = Math.max(currentTime, 0);
        currentTime /= 1000;       
        int minutes = (int) (currentTime / 60);
        int seconds = (int) (currentTime % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    private String formatScore(int score) {
        return String.format("%,d", score);
    }

    public void showMessage(String text) {
        message = text;
        messageOn = true;
    }
    
    public void draw(Graphics2D g2) {
        if (!gp.isStartingEffect) {
            drawHealthBar(g2);
            drawClock(g2);
            drawScore(g2);
            drawKeys(g2);
            drawMenuButton(g2);

            if (showTutorial) {
                drawTutorial(g2); // Vẽ hộp thoại
            }

            if (showEffect) {
                drawImageEffect(g2);
            }

            if(gameFinished) {
                drawGameFinishedScreen(g2);
            }

            if(messageOn) {
                drawMessage(g2);
            }

            if (showKeySequence) {
                drawKeySequence(g2);
            }

            if (showPortalSequence) {
                drawPortalSequence(g2);
            }
        }
    }

    private void drawHealthBar(Graphics2D g2) {
        Composite originalComposite = g2.getComposite();
        try {
            int startY = 3;
            int spacing = 5;
            int heartSize = gp.tileSize;
            int startX = gp.screenWidth - 130 - (heartSize + spacing) * 4;
            
            //vẽ máu thậtthật
            for (int i = 0; i < gp.player.maxHealth; i++) {
                if (i < gp.player.health) {
                    g2.drawImage(heart, 
                            startX + (i * (heartSize + spacing)), 
                            startY, 
                            heartSize, heartSize, null);
                }
            }

            //vẽ máu ảo
            if (gp.player.tempHealth > 0) {
                long remainingTime = gp.player.tempHealthExpireTime - System.currentTimeMillis();
                if (remainingTime > 0) {
                    // Tính alpha dựa trên thời gian còn lại (30 giây = 30,000 ms)
                    float fadeDuration = 30000f; // 30 giây
                    float initialAlpha = 0.7f;
                    float alpha = initialAlpha * (remainingTime / fadeDuration);
                    if (alpha < 0) alpha = 0; // Đảm bảo alpha không âm
                    if (alpha > initialAlpha) alpha = initialAlpha; // Giới hạn alpha tối đa

                    AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
                    g2.setComposite(ac);
                    int tempStartY = startY + heartSize + 5;
                    for (int i = 0; i < gp.player.tempHealth; i++) {
                        g2.drawImage(heart2, 
                                startX + (i * (heartSize + spacing)), 
                                tempStartY, 
                                heartSize, heartSize, null);
                    }
                } else {
                    // Nếu hết thời gian, đặt tempHealth về 0
                    gp.player.tempHealth = 0;
                    gp.player.tempHealthExpireTime = 0;
                }
            }
        } finally {
            g2.setComposite(originalComposite);
        }
    }

    private void drawClock(Graphics2D g2) {
        Font originalFont = g2.getFont();
        Color originalColor = g2.getColor();
        Font timeFont = new Font("Arial", Font.BOLD, 30);
        g2.setFont(timeFont);
        g2.setColor(Color.white);
        String timeText = getFormattedTime();
        FontMetrics fm = g2.getFontMetrics();
        int x = (gp.screenWidth - fm.stringWidth(timeText)) / 2;
        int y = 40;
        g2.setColor(Color.white);
        g2.drawString(timeText, x, y);
        g2.setFont(originalFont);
        g2.setColor(originalColor);
    }

    private void drawScore(Graphics2D g2) {
        Font originalFont = g2.getFont();
        g2.setFont(new Font("Arial", Font.BOLD, 30));
        String scoreText = "Score: " + formatScore(visibleScore);
        int x = 20;
        int y = 40;
        g2.setColor(Color.white);
        g2.drawString(scoreText, x, y);
        g2.setFont(originalFont);
    }

    // phương thức vẽ nút Menu
    private void drawMenuButton(Graphics2D g2) {
        int buttonWidth = 80;
        int buttonHeight = 30;
        int x = gp.screenWidth - buttonWidth - 40; // Căn phải
        int y = 15; // Căn trên

        // Vẽ nền nút
        g2.setColor(MENU_BUTTON_COLOR);
        g2.fillRoundRect(x, y, buttonWidth, buttonHeight, 15, 15);

        // Vẽ viền
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, buttonWidth, buttonHeight, 15, 15);

        // Vẽ text
        g2.setColor(MENU_BUTTON_TEXT_COLOR);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        String text = "MENU";
        int textX = x + (buttonWidth - g2.getFontMetrics().stringWidth(text)) / 2;
        int textY = y + (buttonHeight / 2) + 7;
        g2.drawString(text, textX, textY);

        // Lưu vùng click
        menuButtonRect = new Rectangle(x, y, buttonWidth, buttonHeight);
    }

    private void drawKeys(Graphics2D g2) {
        int keySize = gp.tileSize * 3 / 4; // Kích thước ảnh key
        int x = 20; // Vị trí bắt đầu
        int y = 48 + 45; // Dưới phần Score
        
        // Vẽ hình ảnh key
        g2.drawImage(keyImage, x, y - keySize, keySize, keySize, null);
        
        // Vẽ số lượng key
        g2.setFont(new Font("Arial", Font.BOLD, 30));
        g2.setColor(Color.WHITE);
        g2.drawString("x " + gp.hasKey, x + keySize + 5, y - 5);
    }

    private void drawGameFinishedScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        if (!textSound.clip.isRunning()) {
            textSound.setFile(13);
            textSound.play();
        }
        int boxWidth = gp.screenWidth * 3/4;
        int boxHeight = gp.screenHeight / 2;
        int boxX = (gp.screenWidth - boxWidth) / 2;
        int boxY = (gp.screenHeight - boxHeight) / 2;

        g2.setColor(new Color(30, 30, 30));
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        g2.setColor(gameWon ? new Color(50, 200, 50) : new Color(200, 50, 50));
        g2.setStroke(new BasicStroke(5));
        g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        String text = gameWon ? "You Won!" : "Game Over";
        g2.setFont(new Font("Arial", Font.BOLD, 80));
        g2.setColor(Color.white);
        int x = getXForCenteredText(text, g2);
        int y = boxY + boxHeight/2 - 20;
        g2.drawString(text, x, y);

        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        String scoreText = "Final Score: " + formatScore(getFinalScore());
        x = getXForCenteredText(scoreText, g2);
        y += 60;
        g2.drawString(scoreText, x, y);

        g2.setFont(new Font("Arial", Font.PLAIN, 25));
        String restartText = "Press R to restart";
        x = getXForCenteredText(restartText, g2);
        y += 40;
        g2.drawString(restartText, x, y);
    }

    private void drawMessage(Graphics2D g2) {
        g2.setColor(Color.white);
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.drawString(message, 20, gp.screenHeight - 80);
        messageCounter++;
        if(messageCounter > 120) {
            messageCounter = 0;
            messageOn = false;
        }
    }

    private int getXForCenteredText(String text, Graphics2D g2) {
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return gp.screenWidth / 2 - length / 2;
    }

    public void nextTutorialPage() {
        if (currentTutorialPage < tutorialPages.length - 1) {
            textSound.stop();
            currentTutorialPage++;
            currentCharIndex = 0;
        } else {
            showTutorial = false;
            startTimer(); // Bắt đầu đếm khi hộp thoại kết thúc
        }
    }

    private void drawKeySequence(Graphics2D g2) {
        String fullText = keyMessages[currentKeyPage];
        
        if (fullText.startsWith("[SHOW_IMAGE")) {
            drawImageEffect(g2);
        } else {
            // Từng ký tự một
            if (System.currentTimeMillis() - lastCharTime > CHAR_DELAY && currentCharIndex < fullText.length()) {
                currentCharIndex++;
                lastCharTime = System.currentTimeMillis();
            }
            drawTextBox(g2, fullText, currentCharIndex, 150, "-> tiếp", false);
        }
    }

    private void drawPortalSequence(Graphics2D g2) {
        String fullText = portalMessages[currentPortalPage];
        if (System.currentTimeMillis() - lastCharTime > CHAR_DELAY && currentCharIndex < fullText.length()) {
            currentCharIndex++;
            lastCharTime = System.currentTimeMillis();
        }
        drawTextBox(g2, fullText, currentCharIndex, 150, "-> tiếp", false);
    }

    private void drawTutorial(Graphics2D g2) {
        if (!showTutorial) return;
        String fullText = tutorialPages[currentTutorialPage];
        if (!tutorialSound.clip.isRunning()) {
            tutorialSound.play();
        }
        // Xử lý trang hiệu ứng
        if (fullText.startsWith("[SHOW_IMAGE")) {
            if (!showEffect) { // Chỉ kích hoạt hiệu ứng một lần
                String[] parts = fullText.split(" ");
                String imageName = parts[1].trim();
                String durationStr = parts[2].replace("]", "").trim();
                int duration = Integer.parseInt(durationStr);
                triggerImageEffect(imageName, duration);
            }
            drawImageEffect(g2); // Vẽ ảnh
            return; // Không vẽ nút "Next" cho trang này
        }

        // Từng ký tự một
        if (System.currentTimeMillis() - lastCharTime > CHAR_DELAY && currentCharIndex < fullText.length()) {
            currentCharIndex++;
            lastCharTime = System.currentTimeMillis();
        }
        
        
        drawTextBox(g2, fullText, currentCharIndex, 150, "-> tiếp", false);
    }

    private void drawTextBox(Graphics2D g2, String fullText, int currentIndex, int boxHeight, String buttonLabel, boolean drawButton) {
        int boxWidth = gp.screenWidth - 100;
        int boxX = 50;
        int boxY = 120;

        if (currentIndex >= fullText.length()) {
            if (textSound.clip != null && textSound.clip.isRunning()) {
                textSound.stop();
            }
        }
        else {
            // Chỉ phát khi chưa kết thúc và không phải khoảng trắng
            if (fullText.charAt(currentIndex) != ' ' && !textSound.clip.isRunning()) {
                textSound.loop();
            }
        }
        // Vẽ nền
        g2.setColor(Color.BLACK);
        g2.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);
        g2.setColor(BORDER_COLOR);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 20, 20);

        // Cắt text đến chỉ số hiện tại
        String displayText = fullText.substring(0, Math.min(currentIndex, fullText.length()));
        String[] lines = displayText.split("\n");

        // Vẽ text
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 22));
        int startY = boxY + 40;
        for (String line : lines) {
            g2.drawString(line, boxX + 20, startY);
            startY += 30;
        }

        if (currentTutorialPage == tutorialPages.length - 1) {
            drawButton = false;
        }

        // Vẽ nút nếu cần
        if (drawButton) {
            int buttonWidth = 80;
            int buttonHeight = 30;
            int buttonX = boxX + boxWidth - buttonWidth - 20;
            int buttonY = boxY + boxHeight - buttonHeight - 10;

            g2.setColor(BORDER_COLOR);
            g2.fillRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, 10, 10);
            g2.setColor(Color.WHITE);
            g2.drawString(buttonLabel, buttonX + 15, buttonY + 20);
        
            // Lưu vị trí nút OK để xử lý click
            okButtonRect = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
        } else if (currentTutorialPage < tutorialPages.length - 1) {
            // Hiển thị hướng dẫn click để tiếp tục
            g2.setFont(new Font("Arial", Font.ITALIC, 18));
            String clickText = "Click anywhere to continue...";
            int textWidth = g2.getFontMetrics().stringWidth(clickText);
            g2.drawString(clickText, boxX + boxWidth - textWidth - 20, boxY + boxHeight - 15);
        }
    }

    // Phương thức vẽ hiệu ứng
    private void drawImageEffect(Graphics2D g2) {
        if (currentEffect == null || currentEffect.image == null) return;

        long elapsed = System.currentTimeMillis() - effectStartTime;
        
        // Tạo hiệu ứng nhấp nháy toàn màn hình bằng lớp phủ trắng
        float flashIntensity = (float) Math.abs(Math.sin(elapsed / 200.0)) * 0.6f; // Dùng sin để tạo hiệu ứng mượt
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, flashIntensity));
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        
        // Vẽ ảnh gốc không bị ảnh hưởng bởi opacity
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.SrcOver); // Reset composite
        g2.drawImage(
            currentEffect.image, 
            gp.screenWidth/2 - currentEffect.image.getWidth()/2,
            gp.screenHeight/2 - currentEffect.image.getHeight()/2,
            null
        );
        g2.setComposite(originalComposite);
        
        // Tự động tắt hiệu ứng sau khi đủ thời gian
        if (elapsed > currentEffect.duration) {
            showEffect = false;
        }
    }

    public void triggerKeySequence() {
        showKeySequence = true;
        currentKeyPage = 0;
    }

    private void nextKeyPage() {
        if (currentKeyPage < keyMessages.length - 1) {
            textSound.stop();
            currentKeyPage++;
            checkForImageEffect(keyMessages[currentKeyPage]);
        } else {
            showKeySequence = false;
        }
    }

    private void checkForImageEffect(String text) {
        if (text.startsWith("[SHOW_IMAGE")) {
            String[] parts = text.split(" ");
            String imageName = parts[1].trim();
            String durationStr = parts[2].replace("]", "").trim();
            int duration = Integer.parseInt(durationStr);
            triggerImageEffect(imageName, duration);
        }
    }

    public void triggerPortalSequence() {
        showPortalSequence = true;
        currentPortalPage = 0;
    }

    private void nextPortalPage() {
        if (currentPortalPage < portalMessages.length - 1) {
            currentPortalPage++;
        } else {
            showPortalSequence = false;
            // Kích hoạt kiểm tra key sau khi hội thoại kết thúc
            gp.eventObj.checkPortalAfterDialog();
        }
    }

    // Phương thức trigger effect
    private void triggerImageEffect(String imageName, int duration) {
        BufferedImage img = effectImages.get(imageName);
        if (img != null) {
            currentEffect = new BufferedNameEffect(img, duration);
            effectStartTime = System.currentTimeMillis();
            showEffect = true;
            System.out.println("Triggered effect: " + imageName + ", duration: " + duration + "ms");
        } else {
            System.err.println("Không tìm thấy ảnh: " + imageName);
        }
    }

    public void handleClick(int mouseX, int mouseY) {
        if (gp.isStartingEffect) {
            return;
        }
        if (menuButtonRect != null && menuButtonRect.contains(mouseX, mouseY)) {
            gp.mainFrame.switchToMenu();
            gp.pauseGame();
            return;
        }
        if (showKeySequence) {
            showKeySequence = false;
        } 
        // Xử lý click khi đang hiển thị ảnh effect
        else if (showEffect) { 
            showEffect = false;
            nextTutorialPage();
        } 
        // Cho phép click bất kỳ đâu để tiếp tục khi đang trong tutorial (không phải effect)
        else if (showTutorial && !showEffect) {
            textSound.stop();
            nextTutorialPage();
        }
        if (showKeySequence) {
            textSound.stop();
            if (showEffect) {
                showEffect = false;
            }
            nextKeyPage();
        } else if (showPortalSequence) {
            nextPortalPage();
        } else if (!showTutorial) {
            tutorialSound.stop();
        }
    }

}