
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <stdbool.h>


void show_menu();
void start_game();
void set_count();
void set_base();
void view_stats();
//void exit_game();

int main()
{
    int nNum = 1;
   srand((unsigned int)time(NULL));
       
    int menu_select;
       
    show_menu();
    printf("선택하고자 하는 메뉴를 입력하세요: ");
    scanf_s("%d", &menu_select);

    
   

    if (menu_select == 1) {
        start_game();
    }
    else if (menu_select == 2) {
        exit(0);
    }
    else if (menu_select == 3) {
        exit(0);
    }
    
}

void show_menu() {
    printf("=======================================\n");
    printf("             1. 게임 시작              \n");
    printf("             2. 전적 및 승률             \n");
    printf("             3. 게임 종료              \n");
    printf("=======================================\n");

    
}

void start_game() {
    int inning;
    int out_count = 0;
    int ball_count = 0;
    int strike_count = 0;
    int swing_select;
    int anta;

    printf("\n\n\n======================================================\n");
    printf("게임을 시작합니다.\n");
    printf("======================================================\n\n");


    for (inning = 1;inning < 4; inning++) {
        for (;out_count < 3;) {
            printf("======================================================");
            printf("%d 회\n", inning);
            set_count(ball_count, strike_count, out_count);
            set_base();

            printf("1. 스윙,   2. 거르기\n");
            printf("입력: ");
            scanf_s("%d", &swing_select);

            if (swing_select == 1) //스윙하는 경우
            {
                int anta_random = rand() % 100;
                printf(anta_random);
                
                if (0 <= anta_random || anta_random <= 50) { //스윙 후 안타인 경우
                    anta = 1;
                    //out_count++;
                    if (out_count == 3)
                    {
                        if (inning == 3)
                        {
                            printf("경기가 종료되었습니다.\n");
                        }
                        printf("이닝이 종료되었습니다.\n");

                    }
                    set_count(ball_count, strike_count, out_count); //베이스 설정 해줘야됨
                }



                else if (50 < anta_random || anta_random <= 100) { //스윙 후 아웃인 경우
                    out_count++;
                    if (out_count == 3)
                    {
                        if (inning == 3)
                        {
                            printf("경기가 종료되었습니다.\n");
                        }
                        printf("이닝이 종료되었습니다.\n");

                    }
                    set_count(ball_count, strike_count, out_count);
                }
            }


            else if (swing_select == 2) { //볼을 거르는 경우
                //srand((unsigned)time(NULL));
                int ball_random = rand() % 100;

                if (0 <= ball_random || ball_random <= 50) {  //볼을 거른 후 스트라이크인 경우
                    printf("볼!!\n");
                    strike_count++;
                }

                else if (50 < ball_random || ball_random <= 99) {}
                    printf("스트라이크\n");
                    ball_count++;
                }
            }

        }

    }




void set_count(int ball_count, int strike_count, int out_count) {
    char st_ball_count[8] = "○○○";
    char st_strike_count[6] = "○○";
    char st_out_count[8] = "○○";

    if (ball_count == 0)
        printf("B ○○○\n");
    else if (ball_count == 1)
        printf("B ●○○\n");
    else if (ball_count == 2)
        printf("B ●●○\n");
    else 
        printf("B ●●●\n");


    if (strike_count == 0)
        printf("S ○○\n");
    else if (strike_count == 1)
        printf("S ●○\n");
    else
        printf("S ●●\n");


    if (out_count == 0)
        printf("O ○○\n");
    else if (out_count == 1)
        printf("O ●○\n");
    else
        printf("O ●●\n");
          
}

void set_base() {
    bool base1 = false;
    bool base2 = false;
    bool base3 = false;
    bool home = false;
    char basicbase[3] = "◇";
    char base_in[3] = "◆";

    printf("                      ◇\n");
    printf("                  ↙     ↘\n");
    printf("               ↙           ↘\n");
    printf("            ↙                 ↘\n");
    printf("         ◇                       ◇\n");
    printf("             ↘                ↗\n");
    printf("                ↘          ↗\n");
    printf("                   ↘    ↗\n");
    printf("                      ◇\n");

    /*
    printf("                      ◇\n");
    printf("                  ↙     ↘\n");
    printf("               ↙           ↘\n");
    printf("            ↙                 ↘\n");
    printf("         ◇                       ◆\n");
    printf("             ↘                ↗\n");
    printf("                ↘          ↗\n");
    printf("                   ↘    ↗\n");
    printf("                      ◇\n");

    printf("                      ◆\n");
    printf("                  ↙     ↘\n");
    printf("               ↙           ↘\n");
    printf("            ↙                 ↘\n");
    printf("         ◇                       ◆\n");
    printf("             ↘                ↗\n");
    printf("                ↘          ↗\n");
    printf("                   ↘    ↗\n");
    printf("                      ◇\n");

    printf("                      ◆\n");
    printf("                  ↙     ↘\n");
    printf("               ↙           ↘\n");
    printf("            ↙                 ↘\n");
    printf("         ◆                       ◆\n");
    printf("             ↘                ↗\n");
    printf("                ↘          ↗\n");
    printf("                   ↘    ↗\n");
    printf("                      ◇\n");
    */
}