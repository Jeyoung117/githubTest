#include <stdio.h>
#include <stdlib.h>
#include <time.h>




void main()
{
    int num[3];   // 입력 숫자 배열

    int i, count = 0;  // 정답까지의 횟수
    int n1, n2, n3;  // 3자리 난수 발생 
    int s_count = 0, b_count = 0, o_count = 0;  
        int beg, sip, il;   


        srand(time(NULL));   //난수발생

    n1 = rand() % 10;
    n2 = rand() % 10;
    n3 = rand() % 10;

    puts("야구 게임\n");
    printf("난수 발생 :%d%d%d\n", n1, n2, n3);

    while (1)
    {
        puts("숫자 3개를 순서를 생각하여 입력하시오\n");

        scanf("%d", &num[0]);
        printf("첫번째 숫자:%d\n", num[0]);
        scanf("%d", &num[1]);
        printf("첫번째 숫자:%d\n", num[1]);
        scanf("%d", &num[2]);
        printf("첫번째 숫자:%d\n", num[2]);

        beg = n1;
        sip = n2;
        il = n3;

        if (num[0] == beg)
            s_count += 1;
        if (num[1] == sip)
            s_count += 1;
        if (num[2] == il)
            s_count += 1;

        if (num[0] == sip)
            b_count += 1;
        if (num[0] == il)
            b_count += 1;
        if (num[1] == beg)
            b_count += 1;
        if (num[1] == il)
            b_count += 1;
        if (num[2] == beg)
            b_count += 1;
        if (num[2] == sip)
            b_count += 1;

        for (i = 0;i < 3;i++)
        {
            if (num[i] != beg && num[i] != sip && num[i] != il)
            {
                o_count += 1;
            }
        }

        printf("%d strike !!\n", s_count);
        printf("%d ball !!\n", b_count);
        printf("%d out !!\n\n\n", o_count);

        count++;
        if (s_count == 3)
        {
            printf("정답입니다!!!\n");
            printf("%d 번 만에 정답을 맞추었습니다\n", count);
        }
        s_count = 0, b_count = 0, o_count = 0;

    }



}
