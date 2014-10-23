package viewer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.Vector;
import org.json.*;
import java.util.*;

/**
 * Created by Fez on 10/21/14.
 */
public class MapViewer {

    int width = 1024;
    int height = 1024;
    int maxCircleRadius = 40;
    int maxNumGames = -1; //negative means no cap
    private static final int GOOD_QUALITY = 3500;

    private static final String[] heroesArr = new String[] {"empty", "Anti-Mage", "Axe", "Bane", "Bloodseeker", "Crystal Maiden", "Drow Ranger", "Earthshaker", "Juggernaut", "Mirana", "Morphling", "Shadow Fiend", "Phantom Lancer", "Puck", "Pudge", "Razor", "Sand King", "Storm Spirit", "Sven", "Tiny", "Vengeful Spirit", "Windranger", "Zeus", "Kunkka", "empty", "Lina", "Lion", "Shadow Shaman", "Slardar", "Tidehunter", "Witch Doctor", "Lich", "Riki", "Enigma", "Tinker", "Sniper", "Necrophos", "Warlock", "Beastmaster", "Queen of Pain", "Venomancer", "Faceless Void", "Wraith King", "Death Prophet", "Phantom Assassin", "Pugna", "Templar Assassin", "Viper", "Luna", "Dragon Knight", "Dazzle", "Clockwerk", "Leshrac", "Nature's Prophet", "Lifestealer", "Dark Seer", "Clinkz", "Omniknight", "Enchantress", "Huskar", "Night Stalker", "Broodmother", "Bounty Hunter", "Weaver", "Jakiro", "Batrider", "Chen", "Spectre", "Ancient Apparition", "Doom", "Ursa", "Spirit Breaker", "Gyrocopter", "Alchemist", "Invoker", "Silencer", "Outworld Devourer", "Lycan", "Brewmaster", "Shadow Demon", "Lone Druid", "Chaos Knight", "Meepo", "Treant Protector", "Ogre Magi", "Undying", "Rubick", "Disruptor", "Nyx Assassin", "Naga Siren", "Keeper of the Light", "Io", "Visage", "Slark", "Medusa", "Troll Warlord", "Centaur Warrunner", "Magnus", "Timbersaw", "Bristleback", "Tusk", "Skywrath Mage", "Abaddon", "Elder Titan", "Legion Commander", "empty", "Ember Spirit", "Earth Spirit", "empty", "Terrorblade", "Phoenix"};

    private static final String[] midlane = new String[] {
            "Bloodseeker", "Drow Ranger", "Shadow Fiend", "Puck", "Pudge", "Storm Spirit", "Tiny",
            "Vengeful Spirit", "Zeus", "Kunkka", "Tidehunter", "Sniper", "Warlock", "Queen of Pain"
    };

    private static final Color DIRE_RED = new Color(110, 0, 36, 80);
    private static final Color RADIANT_GREEN = new Color(142, 255, 2, 80);
    JPanel map;
    JLabel minimap;
    JFrame frame;
    Vector<Game> games;

    public void log(String s) {
        System.out.print(s);
    }

    public MapViewer(File directory) {
        games = new Vector<Game>();
        if(directory != null) {
            int countFiles = 0;
            Vector<File> queue = new Vector<File>(Arrays.asList(directory.listFiles()));
            for (File f : queue) {
                countFiles++;
                if (f.isDirectory()) {
                    queue.addAll(Arrays.asList(f.listFiles()));
                } else {
                    Game g = dealWithGameFile(f);
                    if(g != null) {
                        games.add(g);
                    }
                }
                if(countFiles == maxNumGames) break;
            }
            log(countFiles + " files were processed\n");
            log("Number of games = "+games.size()+"\n");
        }

        ImageIcon icon = null;
        try {
            Image img = ImageIO.read(getClass().getResource("../dota_minimap.jpg")).getScaledInstance(width, height, Image.SCALE_DEFAULT);
            icon = new ImageIcon(img);
        } catch (IOException e) {
            e.printStackTrace();
        }
        minimap = new JLabel();
        minimap.setIcon(icon);
        createGUI();
    }

    private void addPanelsToPane(Container pane) {
        pane.setLayout(null);
        map = new JPanel(null);
        for(Game g : games) {
            Vector<Circle> circles = g.getCircles();
            for(Circle c: circles) {
                map.add(c);
                c.setBounds(c.getXCoord(), c.getYCoord(), c.getRadius(), c.getRadius());
            }
        }
        pane.add(map);
        map.add(minimap);
        map.setBounds(0, 0, width, height);
        minimap.setBounds(0, 0, width, height);
    }

    private void createGUI() {
        frame = new JFrame("DOTA2 - Stats Viewer");
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        Container pane = frame.getContentPane();
        addPanelsToPane(pane);
        frame.setDefaultCloseOperation(3);
        frame.setPreferredSize(new Dimension(width, height));
        frame.pack();
        frame.setVisible(true);
    }

    private String readWholeFile(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            String ls = System.getProperty("line.separator");

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            return stringBuilder.toString();
        } catch(Exception e) {

        }
        return "";
    }

    private Game dealWithGameFile(File game) {
        String fileContents = readWholeFile(game);
        JSONObject jsonGame = new JSONObject(fileContents);
        try {
            Game toRet = new Game((JSONObject) jsonGame.get("result"));
            return toRet;
        } catch(JSONException jse) {
            //log(jse.getMessage() + "\n");
            return null;
        }
    }

    float avgKDR = 0.0f;
    float avgLHDR = 0.0f;
    float avgGPXPM = 0.0f;
    int numPlayers = 0;
    int border = 130;

    class Player {

        Circle c;
        long account_id;
        int player_slot;
        int hero_id;
        int kills;
        int deaths;
        int assists;
        int leaver_status;
        int gold;
        int last_hits;
        int denies;
        int gold_per_min;
        int xp_per_min;
        int gold_spent;
        int hero_damage;
        int tower_damage;
        int hero_healing;
        int level;
        int quality;
        int lane;

        boolean isRadiant() {
            return player_slot < 5;
        }

        public Player(JSONObject _player) {
            account_id        = _player.getLong("account_id");
            player_slot       = _player.getInt("player_slot");
            hero_id           = _player.getInt("hero_id");
            kills             = _player.getInt("kills");
            deaths            = _player.getInt("deaths");
            assists           = _player.getInt("assists");
            leaver_status     = _player.getInt("leaver_status");
            gold              = _player.getInt("gold");
            last_hits         = _player.getInt("last_hits");
            denies            = _player.getInt("denies");
            gold_per_min      = _player.getInt("gold_per_min");
            xp_per_min        = _player.getInt("xp_per_min");
            gold_spent        = _player.getInt("gold_spent");
            hero_damage       = _player.getInt("hero_damage");
            tower_damage      = _player.getInt("tower_damage");
            hero_healing      = _player.getInt("hero_healing");
            level             = _player.getInt("level");

            if(Arrays.asList(midlane).contains(heroesArr[hero_id])) {
                lane = 1;
            } else {
                if(Math.random() > 0.5f) {
                    lane = 0;
                } else {
                    lane = 2;
                }
            }

            quality = calculateQuality();
            c = new Circle(calculateX(), calculateY(), calculateRadius(), calculateColor());
//            log("AvgKDR = "+avgKDR+", AvgLHDR = "+avgLHDR+", AvgGPXPM = "+avgGPXPM+"\n");
        }

        private int calculateQuality() {
            float kdr = calculateKDR();
            int lhdr = calculateLHDR();
            int gpxpm = calculateGPXPM();
            int oldNumPlayers = numPlayers;
            numPlayers++;
            avgKDR = ((avgKDR * oldNumPlayers) + kdr) / numPlayers;
            avgLHDR = ((avgLHDR * oldNumPlayers) + lhdr) / numPlayers;
            avgGPXPM = ((avgGPXPM * oldNumPlayers) + gpxpm) / numPlayers;

            return (int)(lhdr + gpxpm + kdr);
        }

        private float calculateKDR() {
            if(deaths == 0)
                return ((float)kills / 1.0f);
            return ((float)kills / (float)deaths);
        }

        private int calculateLHDR() {
            return last_hits + denies;
        }

        private int calculateGPXPM() {
            return gold_per_min + xp_per_min;
        }

        private int calculateX() {
            float qualityF = (float)quality / GOOD_QUALITY;
            if(qualityF > 1.0f) qualityF = 1.0f;
            else if(quality < 0.0f) qualityF = 0.0f;

            if(isRadiant()) {
                //radiant
                if(lane == 1) {
                    //mid lane hero
                    return (int)((width - 2 * border) * qualityF) + border;
                } else {
                    //other heroes
                    if(lane == 0) {
                        //top lane
                        if(qualityF < 0.5f) {
                            return border;
                        } else {
                            return (int)((width - (2 * border)) * (( (qualityF - 0.5f) * 2.0f))) + border;
                        }
                    } else {
                        //bot lane
                        if(qualityF > 0.5f) {
                            return width - border;
                        } else {
                            return (int)((width - 2 * border) * (( (qualityF) * 2.0f))) + border;
                        }
                    }
                }
            } else {
                //dire
                if(lane == 1) {
                    //mid lane hero
                    return (width - 2 * border) - (int)((width - 2 * border) * qualityF) + border;
                } else {
                    //other heroes
                    if(lane == 0) {
                        //top lane
                        if(qualityF > 0.5f) {
                            return border;
                        } else {
                            return (width - 2 * border) - (int)((width - (2 * border)) * (( (qualityF) * 2.0f))) + border;
                        }
                    } else {
                        //bot lane
                        if(qualityF < 0.5f) {
                            return width - border;
                        } else {
                            return (width - 2 * border) - (int)((width - 2 * border) * (( (qualityF - 0.5f) * 2.0f))) + border;
                        }
                    }
                }
            }
        }

        private int calculateY() {
            float qualityF = (float)quality / GOOD_QUALITY;
            if(qualityF > 1.0f) qualityF = 1.0f;
            else if(quality < 0.0f) qualityF = 0.0f;

            if(isRadiant()) {
                //radiant
                if(lane == 1) {
                    //mid lane hero
                    return (height - 2 * border) - (int)((height - 2 * border) * qualityF) + border;
                } else {
                    //other heroes
                    if(lane == 2) {
                        //bot lane
                        if(qualityF < 0.5f) {
                            return height - border;
                        } else {
                            return (height - 2 * border) - (int)((height - (2 * border)) * ((qualityF - 0.5f) *2.0f )) + border;
                        }
                    } else {
                        //top lane
                        if(qualityF > 0.5f) {
                            return border;
                        } else {
                            return (height - 2 * border) - (int)((height - 2 * border) * (( (qualityF ) * 2.0f))) + border;
                        }
                    }
                }
            } else {
                //dire
                if(lane == 1) {
                    //mid lane hero
                    return (int)((height - 2 * border) * qualityF) + border;
                } else {
                    //other heroes
                    if(lane == 2) {
                        //bot lane
                        if(qualityF > 0.5f) {
                            return height - border;
                        } else {
                            return (int)((height - (2 * border)) * ((qualityF / 0.5f))) + border;
                        }
                    } else {
                        //top lane
                        if(qualityF < 0.5f) {
                            return border;
                        } else {
                            return (int)((height - 2 * border) * (( (qualityF -0.5f) * 2.0f))) + border;
                        }
                    }
                }
            }
        }

        private int calculateRadius() {
            float qualityF = (float)quality / GOOD_QUALITY;
            if(qualityF > 1.0f) qualityF = 1.0f;
            else if(qualityF <= 0.1f) qualityF = 0.1f;
            return (int)(maxCircleRadius * qualityF);

        }

        private Color calculateColor() {
            return isRadiant()?RADIANT_GREEN:DIRE_RED;
        }

        public Circle getCircle() {
            return c;
        }
    }

    class Game {
        boolean radiant_win;
        long duration;
        long start_time;
        long match_id;
        long match_seq_num;
        int tower_status_radiant;
        int tower_status_dire;
        int barracks_status_radiant;
        int barracks_status_dire;
        int num_human_players;
        int positive_votes;
        int negative_votes;
        int game_mode;
        Vector<Player> players;

        public Game(JSONObject _game) {
            radiant_win                 = _game.getBoolean("radiant_win");
            duration                    = _game.getLong("duration");
            start_time                  = _game.getLong("start_time");
            match_id                    = _game.getLong("match_id");
            match_seq_num               = _game.getLong("match_seq_num");
            tower_status_radiant        = _game.getInt("tower_status_radiant");
            tower_status_dire           = _game.getInt("tower_status_dire");
            barracks_status_radiant     = _game.getInt("barracks_status_radiant");
            barracks_status_dire        = _game.getInt("barracks_status_dire");
            num_human_players           = _game.getInt("human_players");
            positive_votes              = _game.getInt("positive_votes");
            negative_votes              = _game.getInt("negative_votes");
            game_mode                   = _game.getInt("game_mode");

            JSONArray _players = (JSONArray)_game.get("players");
            players = new Vector<Player>();
            for(int i = 0; i < _players.length(); ++i) {
                players.add(new Player((JSONObject)_players.get(i)));
            }
        }

        public String toString() {
            return "[ " + radiant_win + ", " + duration + ", " + match_id + "]";
        }

        public Vector<Circle> getCircles() {
            Vector<Circle> toRet = new Vector<Circle>();
            for(Player p : players) {
                toRet.add(p.getCircle());
            }
            return toRet;
        }
    }

    class Circle extends JPanel {
        int x, y;
        int radius;
        Color color;

        public Circle(int _x, int _y, int _r, Color _c) {
            x = _x;
            y = _y;
            radius = _r;
            color = _c;
        }

        int getXCoord() {
            return x;
        }

        int getYCoord() {
            return y;
        }

        int getRadius() {
            return radius;
        }

        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(color);
            g2.fillOval(0, 0, radius, radius);
        }
    }

    public static void main(String[] args) {
        MapViewer runner = new MapViewer(new File("/Users/Fez/Downloads/Homework/DotaViewer/src/game_files/"));
    }
}
