import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;

public class KalkulatorPG1 {       //Kalkulator Podatku Giełdowego (z opcjonalnym tworzeniem pliku na kolejny rok)

    public static void main(String[] args) throws FileNotFoundException {

        Scanner scanner = new Scanner(System.in);
        BigDecimal przychod;
        BigDecimal koszty;
        BigDecimal sumaPrzychodow = BigDecimal.valueOf(0);
        BigDecimal sumaKosztow = BigDecimal.valueOf(0);
        BigDecimal dochod;
        BigDecimal podstawa /*= BigDecimal.valueOf(0)*/;
        BigDecimal podstawaZaokraglona;
        BigDecimal podatek;
        BigDecimal podatekDoZaplaty;
        BigDecimal sumaStratDoOdliczenia = BigDecimal.valueOf(0);
        BigDecimal sumaStratDoOdliczeniaWTymRoku = BigDecimal.valueOf(0);
        int y;
        int rok;
        boolean nPlik = true;

        BigDecimal[] wynik = new BigDecimal[5];
        BigDecimal[] stratyOdliczone = new BigDecimal[5];
        BigDecimal[] stratyDoOdliczenia = new BigDecimal[5];
        BigDecimal[] doOdliczeniaWJednymRoku = new BigDecimal[5];





        System.out.println("Podaj rok, dla którego chcesz obliczyć podatek do zapłacenia:");
        try {
            rok = scanner.nextInt();
        }
        catch (java.util.InputMismatchException a) {  //druga i ostatnia szansa na wpisanie poprawnych danych
            System.out.println("Nieprawidłowe dane!");
            System.out.println("Podaj poprawne dane: ");
            scanner = new Scanner(System.in);
            rok = scanner.nextInt();
        }

        System.out.println("Podaj liczbę rachunków, z których masz PIT8C:");
        int rachunki = scanner.nextInt();

        System.out.println("Czy osiągnąleś jakieś przychody i/lub poniosłeś koszty (które możesz odliczyć)" +
                " nie uwzględnione w otrzymanych dokumentach PIT8C? (Tak/Nie)");
        String extras = scanner.next();

        if (extras.compareTo("Tak") * extras.compareTo("T") * extras.compareTo("tak") * extras.compareTo("t") == 0) {

            System.out.println("Podaj przychód nieuwzględniony w otrzymanych dokumentach PIT8C" +
                    " (jeśli poniosłeś/aś tylko koszty - wpisz \"0\":");
            przychod = scanner.nextBigDecimal();
            sumaPrzychodow = sumaPrzychodow.add(przychod);
            System.out.println("Podaj koszty przychodów nieuwzględnione w otrzymanych dokumentach PIT8C" +
                    " (jeśli odnotowałeś/aś tylko przychody - wpisz \"0\":");
            koszty = scanner.nextBigDecimal();
            sumaKosztow = sumaKosztow.add(koszty);
        }


        for (int i = 1; i <= rachunki; i++) {
            System.out.println("Podaj przychód z " + i + ". rachunku:");

            przychod = scanner.nextBigDecimal();
            sumaPrzychodow = sumaPrzychodow.add(przychod);

            System.out.println("Podaj koszty (bez \"-\") uzyskania przychodu z " + i + ". rachunku:");
            koszty = scanner.nextBigDecimal();
            sumaKosztow = sumaKosztow.add(koszty);

            BigDecimal zysk = przychod.add(koszty.negate());

            if (przychod.compareTo(koszty) >= 0) {
                System.out.println("Zysk uzyskany na " + i + ". rachunku wynosi: " + zysk + " zł\n");
            } else {
                System.out.println("Strata poniesiona na " + i + ". rachunku wynosi: " + zysk + " zł");
            }
        }

        System.out.println("Suma przychodów za " + rok + " rok wynosi: " + sumaPrzychodow + " zł");
        System.out.println("Suma kosztów za " + rok + " rok wynosi: " + sumaKosztow + " zł");
        dochod = sumaPrzychodow.add(sumaKosztow.negate());
        System.out.println("Dochód (strata) za " + rok + " rok wynosi: " + dochod + " zł");


        try {    //wczytuje dane z pliku, jeśli jest plik z danymi z zeszłego roku
            File plik = new File(rok - 1 + ".txt");
            Scanner zPliku = new Scanner(plik);


            System.out.print("\nDane z poprzednich 5 lat: ");
            for (y = 4; y >= 0; y--) {
                wynik[y] = zPliku.nextBigDecimal();
                System.out.println("\nDochód (strata) za " + (rok - y - 1) + " rok: "  + wynik[y]);

                if (wynik[y].compareTo(BigDecimal.valueOf(0)) < 0) {
                    stratyOdliczone[y] = zPliku.nextBigDecimal();
                    double smiec = zPliku.nextDouble();
//                    stratyDoOdliczenia[y] = zPliku.nextBigDecimal();

                    stratyDoOdliczenia[y] = wynik[y].add(stratyOdliczone[y].negate());
                    sumaStratDoOdliczenia = sumaStratDoOdliczenia.add(stratyDoOdliczenia[y]);

                    System.out.println("Straty z " + (rok - y - 1) + " roku pozostałe do odliczenia: " +
                            stratyDoOdliczenia[y] + " zł");

                    doOdliczWJednymRoku(y, doOdliczeniaWJednymRoku, stratyDoOdliczenia[y], wynik[y]);

                    sumaStratDoOdliczeniaWTymRoku = sumaStratDoOdliczeniaWTymRoku.add(doOdliczeniaWJednymRoku[y]);
                }
            }
        } catch (java.io.FileNotFoundException a) {       //jeśli nie ma pliku z danymi

            if (dochod.compareTo(BigDecimal.valueOf(0)) <= 0) {
                System.out.println("Czy chcesz stworzyć plik z danymi na przyszły rok?");
                String nowyPlik = scanner.next();
                nPlik = nowyPlik.compareTo("Tak") * nowyPlik.compareTo("T") * nowyPlik.compareTo("tak") * nowyPlik.compareTo("t") == 0;
            }
//            if ((nowyPlik.compareTo("Tak") * nowyPlik.compareTo("T") * nowyPlik.compareTo("tak") * nowyPlik.compareTo("t") == 0)||dochod.compareTo(BigDecimal.valueOf(0)) > 0) {
            if (nPlik||dochod.compareTo(BigDecimal.valueOf(0)) > 0) {

//            if (dochod.compareTo(BigDecimal.valueOf(0)) > 0) {  //jeśli dochód jest > 0 podaj wyniki z wcześniejszych lat,
                // żeby można było obliczyć straty możliwe do odliczenia
                System.out.println("Podaj dochody i straty osiągnięte we wcześniejszych pięciu latach oraz straty odliczone: ");
                for (y = 0; y < 5; y++) {
                    System.out.println("Podaj dochód (stratę z \"-\") za " + (rok - y - 1) + " rok: ");
                    wynik[y] = scanner.nextBigDecimal();

                    if (wynik[y].compareTo(BigDecimal.valueOf(0)) < 0) {
                        System.out.println("Podaj wysokość straty z " + (rok - y - 1) + " roku odliczoną w latach późniejszych:");
                        stratyOdliczone[y] = scanner.nextBigDecimal();

                        stratyDoOdliczenia[y] = wynik[y].add(stratyOdliczone[y].negate());
                        sumaStratDoOdliczenia = sumaStratDoOdliczenia.add(stratyDoOdliczenia[y]);

                        System.out.println("Straty z " + (rok - y - 1) + " roku pozostałe do odliczenia: " +
                                stratyDoOdliczenia[y] + " zł");

                        doOdliczWJednymRoku(y, doOdliczeniaWJednymRoku, stratyDoOdliczenia[y], wynik[y]);

                        System.out.println("");
                        sumaStratDoOdliczeniaWTymRoku = sumaStratDoOdliczeniaWTymRoku.add(doOdliczeniaWJednymRoku[y]);
                    }
                }

//                System.out.println("\nSuma strat do odliczenia: " + sumaStratDoOdliczenia + " zł");
//                System.out.println("Suma strat możliwa do odliczenia w tym roku: " + sumaStratDoOdliczeniaWTymRoku + " zł\n");

            }
//            else { //dochód <= 0
//
////                podstawa = BigDecimal.valueOf(0);
////                System.out.println("Podstawa podatku za " + rok + " rok wynosi: " + podstawa + " zł");
//            }
        } finally {

//        }

            System.out.println("\nSuma strat do odliczenia: " + sumaStratDoOdliczenia + " zł");
            System.out.println("Suma strat możliwa do odliczenia w tym roku: " + sumaStratDoOdliczeniaWTymRoku + " zł\n");



            if (dochod == BigDecimal.valueOf(0)) {
//            podatekDoZaplaty = BigDecimal.valueOf(0);
                podstawa = BigDecimal.valueOf(0);
//            podstawaZaokraglona = podstawa;
            } else {
//            if (podatek.compareTo(sumaStratDoOdliczeniaWTymRoku.negate()) > 0) {
                if (dochod.compareTo(sumaStratDoOdliczeniaWTymRoku.negate()) > 0) {
//                podatekDoZaplaty = podatek.add(sumaStratDoOdliczeniaWTymRoku);
                    podstawa = dochod.add(sumaStratDoOdliczeniaWTymRoku);
//                podstawaZaokraglona = podstawa;

//                podstawaZaokraglona = podstawaZaokraglona.setScale(0, RoundingMode.HALF_UP);

                } else {
//                podatekDoZaplaty = BigDecimal.valueOf(0);
                    podstawa = BigDecimal.valueOf(0);
//                podstawaZaokraglona = podstawa;
                }

            }


//        podatek = stawka.multiply(razem).divide(BigDecimal.valueOf(100));
//        podstawaZaokraglona = podstawa;
//        podstawaZaokraglona = podstawaZaokraglona.setScale(0, RoundingMode.HALF_UP);
//

            podstawaZaokraglona = podstawa.setScale(0, RoundingMode.HALF_UP);
            System.out.println("Podstawa podatku za " + rok + " rok wynosi: " + podstawaZaokraglona + " zł");

            System.out.println("Podaj stawkę (w %) podatku od dochodów kapitałowych za " + rok + " rok:");
            BigDecimal stawka = scanner.nextBigDecimal();

            podatek = stawka.multiply(podstawaZaokraglona).divide(BigDecimal.valueOf(100)/*, RoundingMode.HALF_UP*/);
            System.out.println("Podatek za " + rok + " rok wynosi: " + podatek + " zł\n");


            podatekDoZaplaty = podatek.setScale(0,RoundingMode.HALF_UP);
            System.out.println("Podatek należny (do zapłaty) za " + rok + " rok wynosi: "
                    + podatekDoZaplaty + " zł\n");


            if (dochod.compareTo(BigDecimal.valueOf(0)) > 0) {    //jeśli dochód za obliczany rok jest dodatni to
                //podaje i przelicza wyniki podatkowe za poprzednie 5 lat
                //jeśli nie, to nie posiada danych, bo wcześniej nie były podawane

//            BigDecimal stratyOdliczoneWTymRoku = podatekDoZaplaty.add(podatek.negate());

                BigDecimal stratyOdliczoneWTymRoku = odliczone(dochod, podstawa, sumaStratDoOdliczenia);

/*
            for (y = 4; y >= 0; y--) {
                System.out.print("Dochód (strata) za " + (rok - y - 1) + " rok: " + wynik[y]);
                if (wynik[y].compareTo(BigDecimal.valueOf(0)) < 0) {
                    System.out.print(",    Straty pozostałe do odliczenia: " + stratyDoOdliczenia[y]);
                    System.out.print(",    możliwe do odliczenia w jednym roku: " + doOdliczeniaWJednymRoku[y]);
                }
                System.out.println("");
            }
*/

                przeliczStrat(wynik, stratyDoOdliczenia, doOdliczeniaWJednymRoku, rok, stratyOdliczoneWTymRoku);
                // przelicza straty z lat poprzednich o straty odpisane w tym roku


            }

            if (nPlik) {
                przeliczDanych(dochod, wynik, stratyDoOdliczenia, doOdliczeniaWJednymRoku);
                // aktualizuje dane do wykorzystania w przyszłym roku

                wydrukDanych(wynik, stratyDoOdliczenia, rok);
                //wypisuje w konsoli dane do wykorzystania w przyszłym roku

                zapisDanych(wynik, stratyDoOdliczenia, rok);
                //zapisuje do pliku o nazwie (aktualny rok).txt dane do wykorzystania w przyszłym roku

//            PrintWriter zapis = new PrintWriter(rok + ".txt");   //ZAPIS DANYCH Z OPISEM DO PLIKU: (aktualnyRok).TXT
//            for (y = 4; y >= 0; y--) {
//                zapis.print("Dochód(Strata)Za" + (rok - y) + "Rok: " + wynik[y].toString().replace('.',','));
//                if (wynik[y].compareTo(BigDecimal.valueOf(0)) < 0) {
//                    zapis.print("  StratyOdliczone: " + (wynik[y].add(stratyDoOdliczenia[y].negate())).toString().replace('.',','));
//                    zapis.print("    StratyPozostałeDoOdliczeniaWKolejnychLatach: " + (stratyDoOdliczenia[y]).toString().replace('.',','));
//                }
//                zapis.println("");
//            }
//            zapis.close();
            }

            //}

        }

    }

    private static void doOdliczWJednymRoku(int y, BigDecimal[] doOdliczeniaWJednymRoku, BigDecimal bigDecimal1, BigDecimal bigDecimal) {
        if (bigDecimal1.compareTo(bigDecimal.divide(BigDecimal.valueOf(2))) > 0) {
            doOdliczeniaWJednymRoku[y] = bigDecimal1.setScale(2, RoundingMode.HALF_DOWN);
        } else {
            doOdliczeniaWJednymRoku[y] = bigDecimal.divide(BigDecimal.valueOf(2)).setScale(2, RoundingMode.HALF_DOWN);
        }
        System.out.println("Możliwe do odliczenia w jednym roku: " + doOdliczeniaWJednymRoku[y] + " zł");
    }

    private static BigDecimal odliczone(BigDecimal dochod, BigDecimal podstawa, BigDecimal sumaStratDoOdliczenia) {
        BigDecimal stratyOdliczoneWTymRoku = podstawa.add(dochod.negate());
        System.out.println("Odliczone w tym roku: " + stratyOdliczoneWTymRoku + " ");
        sumaStratDoOdliczenia = sumaStratDoOdliczenia.add(stratyOdliczoneWTymRoku.negate());
        System.out.println("Suma strat z poprzednich pięciu lat jeszcze nieodliczona: " + sumaStratDoOdliczenia + " zł\n");
        return stratyOdliczoneWTymRoku;
    }

    private static void przeliczStrat(BigDecimal[] wynik, BigDecimal[] stratyDoOdliczenia, BigDecimal[] doOdliczeniaWJednymRoku, int rok, BigDecimal stratyOdliczoneWTymRoku) {
        int y;
        System.out.println("Przeliczenie strat do odliczenia:");
        BigDecimal zmniejszenieOdliczenia = stratyOdliczoneWTymRoku;

        for (y = 4; y >= 0; y--) {

            System.out.print("Dochód (strata) za " + (rok - y - 1) + " rok: " + wynik[y]);
            if (wynik[y].compareTo(BigDecimal.valueOf(0)) < 0) {

//                    System.out.print(", do odliczenia było: " + stratyDoOdliczenia[y] + " ");
//                    System.out.print(", odliczoneWTymRoku: " + stratyOdliczoneWTymRoku + " ");
//                    System.out.print(", zmniejszenie odl.: " + zmniejszenieOdliczenia + " ");
//                    System.out.print(", możliwe do odl w 1 r.: " + doOdliczeniaWJednymRoku[y] + " ");

                if (doOdliczeniaWJednymRoku[y].compareTo(zmniejszenieOdliczenia) < 0) {
                    if (zmniejszenieOdliczenia.compareTo(BigDecimal.valueOf(0)) > 0) {
                        zmniejszenieOdliczenia = BigDecimal.valueOf(0);
                    }
                    stratyDoOdliczenia[y] = stratyDoOdliczenia[y].add(zmniejszenieOdliczenia.negate());
                    zmniejszenieOdliczenia = zmniejszenieOdliczenia.add(stratyOdliczoneWTymRoku.negate());
                } else {
                    zmniejszenieOdliczenia = zmniejszenieOdliczenia.add(doOdliczeniaWJednymRoku[y].negate());
                    stratyDoOdliczenia[y] = stratyDoOdliczenia[y].add(doOdliczeniaWJednymRoku[y].negate());
                }

                if (y == 4) {
                    System.out.print(";  Straty, których nie da się odliczyć w kolejnych latach (przepadły): "
                            + stratyDoOdliczenia[y]);
                } else {

                    System.out.print(";  Straty pozostałe do odliczenia w kolejnych latach: " + stratyDoOdliczenia[y]);
                }

            }

            System.out.println("");
        }
        System.out.println("");
    }

    private static void wydrukDanych(BigDecimal[] wynik, BigDecimal[] stratyDoOdliczenia, int rok) {
        int y;
        System.out.println("Dane na kolejny rok:");
        for (y = 4; y >= 0; y--) {
            System.out.print("Dochód (strata) za " + (rok - y) + " rok: " + wynik[y]);
            if (wynik[y].compareTo(BigDecimal.valueOf(0)) < 0) {
                System.out.print(";  Straty odliczone: " + wynik[y].add(stratyDoOdliczenia[y].negate()));
                System.out.print(";    Straty pozostałe do odliczenia w kolejnych latach: " + stratyDoOdliczenia[y]);
            }
            System.out.println("");
        }
    }

    private static void przeliczDanych(BigDecimal dochod, BigDecimal[] wynik, BigDecimal[] stratyDoOdliczenia, BigDecimal[] doOdliczeniaWJednymRoku) {
        int y;
        for (y = 4; y > 0; y--) {
            wynik[y] = wynik[y - 1];
            if (wynik[y].compareTo(BigDecimal.valueOf(0)) < 0) {
                stratyDoOdliczenia[y] = stratyDoOdliczenia[y - 1];
                doOdliczeniaWJednymRoku[y] = doOdliczeniaWJednymRoku[y - 1];
            }
        }
        wynik[0] = dochod;
        if (dochod.compareTo(BigDecimal.valueOf(0)) < 0) {
            stratyDoOdliczenia[0] = dochod;
//                doOdliczeniaWJednymRoku[0] = BigDecimal.valueOf(0);
        }
    }

    private static void zapisDanych(BigDecimal[] wynik, BigDecimal[] stratyDoOdliczenia, int rok) throws FileNotFoundException {
        int y;
        PrintWriter zapis = new PrintWriter(rok + ".txt");   //ZAPIS DANYCH DO PLIKU: (aktualnyRok).TXT (same liczby)
        for (y = 4; y >= 0; y--) {
            zapis.print(wynik[y].toString().replace('.',','));
            if (wynik[y].compareTo(BigDecimal.valueOf(0)) < 0) {
                zapis.print("   " + (wynik[y].add(stratyDoOdliczenia[y].negate())).toString().replace('.', ','));
                zapis.print("   " + (stratyDoOdliczenia[y]).toString().replace('.', ','));
            }
            zapis.println("");
        }
        zapis.close();
    }
}



