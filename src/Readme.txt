==================Tema2APD==============
MyDispathcer:
    extind clasa Dispathcer,iar pentru constructor apelez constructor ul din clasa Parinte
    addTask:
    Round Robin:
        am folosit variabila "taskAdded" care este de tipul AtomicInteger pentru a nu avea probleme de sincronizare atunci cand
        trebuie sa aflu la ce nod trimit task ul (sa nu am probleme de sincronizare la index)
        aflu index ul dupa formula din enunt,adaug task ul la host ul cu index ul respectiv si incrementez variabila "taskAdded"
    SHORTEST_QUEUE:
        am folosit cuvantul syncronized pentru a defini o regiune critica
        regiunea critica:
            Iterez prin fiecare host si aflu lungimea cozii de task uri (metoda getQueueSize)
            aflu index ul host ului cu coada minima
            daca exista mai multe host uri cu coada minima aleg primul host cu coada minima
            adaug task ul la host ul cu index ul respectiv
    SIZE_INTERVAL_TASK_ASSIGNMENT:
        am folosit ConcurentHashMap pentru a fi sigur ca nu am probleme de sincronizare
        iterez prin fiecare host si daca acesta nu are tipul de task acceptat in ConcurrentHashMap atunci adaug tipul task ului 
        primit la indexul host ului  (metoda putIfAbsent)
        pentru fiecare task extrag hostul care are acelasi tip de task
        adaug task ul la host ul respectiv
    Least Work Left:
        acelasi lucru ca la SIZE_INTERVAL_TASK_ASSIGNMENT doar ca in loc sa aflu lungimea cozii de task uri aflu
        durata totala a task urilor metoda getWorkLeft 


MyHost:
    extind clasa Host 
    creez coada de prioritati  si folosesc o clasă internă anonimă care implementează interfața Comparator pentru 
        a sorta task urile dupa prioritate in ordine descrescatoare.Daca au aceeasi prioritate le sortez dupa timpul de start
        adica in ce oridine au venit
    am creat o variabila pentru a retine task ul in executie , o variabila pentru a opri executia metodei run atunci cand
        a fost apelata metoda shutdown si un semafor pentru a realiza sincronizarea intre task urile ce trebuiesc executate
    ma folosesc de clasa Timer pentru a masura timpul de cand s a pornit host ul si implicit timpul de start al task urilor

    addTask:
        adaug task ul in coada de prioritati 
        daca exista un task care este in executie, are prioritate mai mica decat task ul primit, task ul curent poate fi preemtat,
            timpul de start al task ului este egal cu timpul curent rotunjit la secunde si diferenta de timp de cand am pornit
            simularea pentru task ul curent si task ul care a venit acum este mai mica de 100 milisecunde , atunci:
               trimit un release la semafor deoarece task ul  curent trebuie preemtat
        metoda este syncronized pentru a nu avea probleme de sincronizare atunci cand se adauga un task in coada de prioritati
            si cand se verifica daca exista un task care se executa in acel moment
    
Motivatie:
  am ales sa folosesc un semafor pentru a realiza sincronizarea intre task urile ce trebuiesc executate deoarece:
    din cauza overhead ului produs de procesul de sincronizare,exista sansa sa primesc un task mai tarziu decat atunci cand 
    verific in run daca task ul curent ar trebui preemtat,atunci pot sa ajung la situatia in care task ul curent
    sa ruleze simularea de executie (1 secunda) la un moment de timp in care trebuia pus in asteptare si task ul venit mai taziu
    sa fie cel executat 

Explicatie: 
    dupa mai multe rulari de teste am observat ca overhead ul produs general este de 0.01 secunde .Am adaugat o marja de 0.1 secunde.
    Astfel,daca ajunge un pachet intarziat, timpul la care ajunge ar trebui sa fie maxim mai mare cu 0.1 maxim decat cel de start.
    Daca nu calculam acest timp,ajungeam la situatia in care :
Exemplu: la momentul de timp 1.1 secunde am pornit simularea task ului curent
        la momentul de timp 2.00 secunde am primit o notificare,
        nu are rost sa preemtez task ul curent deoarece ,dupa ce isi termina executia task ul curent,
        o sa fie preemtat si task ul urmator o sa porneasca la momentul de timp potrivit
    Am folosit aceasta abordare pentru a calcula mai usor timpul ramas fiecarui thread,sa scad 1000 de milisecunde din timpul ramas
        decat sa scad (de ex 900 milisecunde)


    run:
    cat timp nu a fost apelata functia shutdown:
        daca exista un task in coada de prioritati,timpul de start al task ului este mai mic decat timpul curent
        si nu exista nici un task care se executa in acel moment, atunci:
            extrag task ul din coada de prioritati (am folosit synchronized pe instanta curenta pe post de zavor ca
                sa nu existe probleme de sincronizare atunci cand celelalte metode folosesc coada de prioritati)
        daca exista un task care este pus in executie  verific sa vad daca acesta ar trebui preemtat 
            daca da: adaug in coada de prioritati task ul curent si scot primul task din coada
                    (am folosit synchronized pe instanta curenta pe post de zavor)
        simulez executia task ului:
            apelez metoda tryAcquire din semafor pentru a pune thread ul curent in asteptare maxim 1000 de milisecunde
                daca nu primesc nici un release
            daca primesc un release,preemtez task ul curent  si simulez exectuia de 1 secunda 
            daca nu ,las task ul curent sa ruleze simularea de executie pana la final
            scad din timpul ramas 1000 de milisecunde
            daca timpul ramas a devint 0 atunci task ul curent s a terminat(apelez metoda finish) si scot task ul curent
                (variabila care retine ce task se executa in acel moment devine  null) 
                (am folosit synchronized pe instanta curenta pe post de zavor)

    getQueueSize:
        returnez dimensiunea cozii de prioritati +1 daca exista un task care se executa in acel moment 
        am folosit synchronized pe instanta curenta pe post de zavor, cat timp calculez size ul 
        si verific daca exista un task care se executa in acel moment

    
    getWorkLeft:
        returnez timpul ramas pana la finalizarea task urilor din coada de prioritati + 
        timpul ramas de executie al task ului curent daca exista un task care se executa in acel moment
        (iterez prin coada de prioritati si adun timpul ramas de executie al fiecarui task)
        am folosit synchronized pe instanta curenta pe post de zavor cat timp calculez size ul
        si verific daca exista un task care se executa in acel moment


                
             
                



 

    

