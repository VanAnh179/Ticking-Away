package main;

import javax.swing.*;
import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class GradientTitleLabel extends JComponent {
    private MainFrame mainFrame;
    private final String text;
    private final Font font;
    private GamePanel gp = new GamePanel(mainFrame);

    public GradientTitleLabel(String text, Font font) {
        this.text = text;
        this.font = font;
        setPreferredSize(new Dimension(gp.screenWidth / 3, gp.screenHeight / 25)); // chỉnh theo ý muốn
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setFont(font);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = (getHeight() + fm.getAscent()) / 2 - 30;

        // Gradient màu chữ
        GradientPaint gradient = new GradientPaint(230, 0, Color.WHITE, getWidth() - 30, 0, new Color(191, 64, 191));
        g2.setPaint(gradient);

        // Tạo hình ảnh đệm để xử lý shadow và blur
        BufferedImage textImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D tg = textImage.createGraphics();
        tg.setFont(font);
        tg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ bóng đổ (màu hồng đậm + dày hơn)
        tg.setColor(new Color(93, 63, 211)); // màu hồng mờ
        for (int i = 0; i < 6; i++) {
            tg.drawString(text, x + i - 7, y + 8);
        }

        // Tạo hiệu ứng blur cho shadow
        float[] blurKernel = {
            1f / 16, 2f / 16, 1f / 16,
            2f / 16, 4f / 16, 2f / 16,
            1f / 16, 2f / 16, 1f / 16
        };
        ConvolveOp blurOp = new ConvolveOp(new Kernel(3, 3, blurKernel));
        BufferedImage blurred = blurOp.filter(textImage, null);
        g2.drawImage(blurred, 0, 0, null);

        // Vẽ outline viền hồng dày hơn
        tg.setColor(new Color(128, 0, 128));
        tg.setStroke(new BasicStroke(2f));
        tg.draw(new TextLayout(text, font, tg.getFontRenderContext()).getOutline(AffineTransform.getTranslateInstance(x, y)));

        // Vẽ chữ chính
        tg.setPaint(gradient);
        tg.drawString(text, x - 10, y);
        tg.dispose();

        // Vẽ lại vào panel chính
        g2.drawImage(textImage, 0, 0, null);
        g2.dispose();
    }

    // hiệu ứng Neon extends từ JLabel
    // public GradientTitleLabel(String text) {
    //     super(text);
    //     setHorizontalAlignment(SwingConstants.CENTER);
    //     setFont(new Font("Serif", Font.BOLD, 100));
    //     setForeground(Color.WHITE);
    // }

    // @Override
    // protected void paintComponent(Graphics g) {
    //     Graphics2D g2 = (Graphics2D) g.create();
    //     String text = getText();
    //     Font font = getFont();

    //     // Kích hoạt khử răng cưa
    //     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    //     g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    //     // Vị trí trung tâm
    //     FontMetrics fm = g2.getFontMetrics(font);
    //     int textWidth = fm.stringWidth(text);
    //     int textHeight = fm.getAscent();
    //     int x = (getWidth() - textWidth) / 2;
    //     int y = (getHeight() + textHeight) / 2 - 10;

    //     // Tạo TextLayout để có viền và đổ bóng
    //     TextLayout layout = new TextLayout(text, font, g2.getFontRenderContext());
    //     AffineTransform transform = AffineTransform.getTranslateInstance(x, y);
    //     Shape outline = layout.getOutline(transform);

    //     // Đổ bóng hồng nhạt lớn (giả làm blur)
    //     g2.setColor(new Color(255, 105, 180, 80)); // Hồng nhạt trong suốt
    //     for (int i = 1; i <= 8; i++) {
    //         AffineTransform shadowTransform = AffineTransform.getTranslateInstance(x + i * 0.7, y + i * 0.7);
    //         Shape shadow = layout.getOutline(shadowTransform);
    //         g2.fill(shadow);
    //     }

    //     // Vẽ viền hồng neon
    //     g2.setColor(new Color(255, 20, 147)); // Neon pink
    //     g2.setStroke(new BasicStroke(4f));
    //     g2.draw(outline);

    //     // Gradient từ trắng → hồng → trắng
    //     GradientPaint gradient = new GradientPaint(
    //         x, y - textHeight, Color.WHITE,
    //         x + textWidth / 2f, y,
    //         new Color(255, 182, 193), // LightPink
    //         true
    //     );
    //     g2.setPaint(gradient);
    //     g2.fill(outline);

    //     g2.dispose();
    // }
}
