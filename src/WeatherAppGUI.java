import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGUI extends JFrame {
    public WeatherAppGUI() {
        //gui setup and add title
        super ("Weather App");

        //config the gui to end program process once it is closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //gui size set
        setSize(450, 650);

        //load gui at the center of screen
        setLocationRelativeTo(null);

        //make layout manager null to manually position our components within gui
        setLayout(null);

        //prevent gui resize
        setResizable(false);

        addGuiComponents();
    }

    private void addGuiComponents() {
        //search bar
        JTextField searchTextField = new JTextField();

        //set location and component size
        searchTextField.setBounds(15, 15, 351, 45);

        //change font and style of text
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);

        //search button
        JButton searchButton = new JButton(loadImage("src/assets/img/search.png"));

        //cursor to hand cursor
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        add(searchButton);

        //weather image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/img/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        //Temperature
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        //center the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        //wearther condition description
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        //humidity image
        JLabel humidityImage = new JLabel(loadImage("src/assets/img/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        //humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        //windspeed
        JLabel windspeedImage = new JLabel(loadImage("src/assets/img/windspeed.png"));
        windspeedImage.setBounds(220, 500, 74, 66);
        add(windspeedImage);

        //windseed text
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);
    }

    //to create image in our gui
    private ImageIcon loadImage(String resourcePath) {
        try{
            //read img
            BufferedImage image = ImageIO.read(new File(resourcePath));

            //return image for the component to render it
            return new ImageIcon(image);
        }catch (IOException e){
            e.printStackTrace();
        }

        System.out.println("Could not find resource");
        return null;
    }
}
