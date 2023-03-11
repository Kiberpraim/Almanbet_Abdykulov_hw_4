import java.util.Random;

public class Main {
    /*

        "Boss" - он один и нападает первым нанося урон персонжем. У босса есть "Defence", это его способность и уязвимость.
    Способность босса в том что выбрав тип (атаки) героя может предотвратить его способность (если босс выберит Тора то,
    Тор не сможет оглушить босса, также способностями остальных) но если выберит Медика, то Босс сам востанавливает своё здаровие.
    Уязвимость в том что выбрав тип (атаки) героя открывает возможность получть от него критический урон,
    но иногда это урон ровняется обычному урону героя или нулю.

    "Heroes" - их несколько и они наносят ответный удар по мере возможности используя свои способности.

    */
    public static int bossHealth = 1400;
    public static int bossDamage = 100;
    public static String bossDefence;
    public static int[] heroesHealth = {270, 260, 250, 300, 600, 250, 260, 270};
    public static int[] heroesDamage = {25, 30, 35, 0, 20, 25, 30, 35};
    public static String[] heroesType = {"Warrior", "Magical", "Kinetic", "Medic", "Golem", "Lucky", "Berserk", "Thor"};
    public static int roundNumber = 0; // счётчик раунда.
    public static int medicHealthTo = -1; // для вывода статистику.
    public static boolean luckyEvasion; // для способности Лаки и для вывода статистику.
    public static int berserkBlock; // для способности Берсерк и для вывода статистику.
    public static boolean thorStun; // для способности Берсерк и для вывода статистику.
    public static int[] damage = new int[heroesDamage.length]; // для вывода конечного урона (для статистики).

    public static void main(String[] args) {  //движок
        printStatistics();
        while (!isGameFinished()) {
            playRound();
        }
    }

    public static void playRound() {
        roundNumber++;
        chooseBossDefence();
        bossHits();
        heroesHit();
        printStatistics();
    }

    public static void chooseBossDefence() {
        Random random = new Random();
        int randomIndex = random.nextInt(heroesType.length);
        bossDefence = heroesType[randomIndex];
        if (bossDefence == "Medic"){                        //для способности босса
            bossHealth += 100;
        }
    }

    public static void bossHits() {
        int damage;
        Random random = new Random();
        for (int i = 0; i < heroesHealth.length; i++) {

            if(thorStun){break;}            //способность Тора

            damage = bossDamage;

            if (heroesHealth[i] > 0) {

                luckyEvasion = random.nextBoolean();
                if (heroesType[i] == "Lucky" && luckyEvasion && bossDefence != "Lucky") { //способность Лаки
                    continue;
                }

                if (heroesHealth[4] > 0 && bossDefence != "Golem"){     //способность Голема
                    heroesHealth[4] = heroesHealth[4] - bossDamage / 5;
                    damage = bossDamage / 5 * 4;
                }

                if (heroesType[i] == "Berserk" && bossDefence != "Berserk") {   //спобность Берсерка
                    berserkBlock = damage / 5 * 2;
                    damage = bossDamage - berserkBlock;
                }

                if (heroesHealth[i] - damage < 0) {
                    heroesHealth[i] = 0;
                }else {
                    heroesHealth[i] = heroesHealth[i] - damage;
                }
                if (heroesType[i] == "Berserk" && heroesHealth[i] <= 0){
                    berserkBlock = 0;           //для обнуления сособности Берсерка после его смерти (для статистики)
                }

            }
        }
    }
    public static void heroesHit() {
        Random random = new Random();
        thorStun = false; // для обнуленин способности Тора
        for (int i = 0; i < heroesDamage.length; i++) {
            damage[i] = heroesDamage[i];
            if (heroesHealth[i] > 0 && bossHealth > 0) {

                if (heroesType[i] == "Medic"){
                    for (int j = 0; j < heroesHealth.length; j++) {
                        if (heroesHealth[j] < 100 && heroesType[j] != "Medic") {
                            heroesHealth[j] = heroesHealth[j] + 50;     //способность Медика
                            medicHealthTo = j;
                            break;
                        }
                    }
                }

                if (heroesType[i] == "Berserk"){
                    damage[i] = heroesDamage[i] + berserkBlock; //спобность Берсерка
                }

                if (heroesType[i] == "Thor" && bossDefence != "Thor"){ //для активации способности Тора
                    thorStun = random.nextBoolean();
                }

                if (heroesType[i] == bossDefence) {
                    int coefficient = random.nextInt(10); // коэффициент критического урона 0-9
                    damage[i] = heroesDamage[i] * coefficient; // критический урон
                }
                if (bossHealth - damage[i] < 0) {
                    bossHealth = 0;
                } else {
                    bossHealth = bossHealth - damage[i];
                }
            }
        }
    }

    public static boolean isGameFinished() {
        if (bossHealth <= 0) {
            System.out.println("\n\t!!! Heroes won !!!\n");
            return true;
        }
        /*if (heroesHealth[0] <= 0 && heroesHealth[1] <= 0 && heroesHealth[2] <= 0) {
            System.out.println("Boss won!!!");
            return true;
        }
        return false;*/
        boolean allHeroesDead = true;
        for (int i = 0; i < heroesHealth.length; i++) {
            if (heroesHealth[i] > 0) {
                allHeroesDead = false;
                break;
            }
        }
        if (allHeroesDead) {
            System.out.println("\n\t!!! Boss won !!!\n");
        }
        return allHeroesDead;
    }

    public static void printStatistics() {
        System.out.println("\n * * * * *   " + " ROUND " + roundNumber + "    * * * * *\n");
        /* String defence;
        if (bossDefence == null) {
            defence = "No defence";
        } else {
            defence = bossDefence;
        }*/
        System.out.println("Boss health: " + bossHealth + " damage: " + bossDamage
                + " defence: " + (bossDefence == null ? "No defence" : bossDefence));

        for (int i = 0; i < heroesHealth.length; i++) {

            if (heroesHealth[i] <= 0){ // чтобы не выводить в статистику умерших героев
                continue;
            }

            System.out.println(heroesType[i] + " health: " + heroesHealth[i]
                    + (heroesType[i] == bossDefence ? " critical damage: " : " damage: ") + damage[i]);
        }

        if (bossHealth > 0) { // для статистики способностей.

            System.out.println("\n---  Ability events:  ---");
            if (medicHealthTo > -1) {
                System.out.println("Medic: health to " + heroesType[medicHealthTo] + " + 50");
            }
            if (luckyEvasion) {
                System.out.println("Lucky evasion.");
            }
            if (berserkBlock > 0) {
                System.out.println("Berserk block: " + berserkBlock);
            }
            if (thorStun) {
                System.out.println("Thor stunned the boss.");
            }
            if (bossDefence == "Medic") {
                System.out.println("Boss health + 100.");
            }
            medicHealthTo = -1; // для обнуления
            luckyEvasion = false; // для обнуления
        }
    }
}
