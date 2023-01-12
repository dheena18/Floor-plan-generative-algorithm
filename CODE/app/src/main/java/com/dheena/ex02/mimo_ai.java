package com.martinstacey.ex02;

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class mimo_ai extends PApplet {

boolean inandroid = false;
boolean keybisopen = false;
float scand = 1;
String [] roomsIN =  {"Li", "Be", "Ba", "Ki"};
int [] lockIN = {0, 0};
String [][] roomDATAIN={{"Be", "Bedroom", "14.4", "8.8", "24.0", "4.0", "3.2", "4.8", "1.00", "Li", "1"}, {"Ba", "Bathroom", "4.8", "3.2", "8.0", "2.4", "1.6", "3.2", "0.98", "Be", "1"}, {"Cl", "Closet", "3.2", "0.8", "12.8", "0.8", "0.8", "0.8", "0.50", "Be", "2"}, {"Di", "Dining", "12.0", "8.8", "24.0", "4.0", "3.2", "4.8", "0.83", "Li", "0"}, {"Ki", "Kitchen", "3.2", "0.8", "12.8", "0.8", "0.8", "0.8", "0.25", "Li", "2"}, {"La", "Laundry", "4.0", "3.2", "4.8", "2.4", "1.6", "3.2", "0.33", "Li", "1"}, {"Li", "Living", "20.0", "16.0", "24.0", "4.8", "3.2", "6.4", "0.50", "", "0"}, {"Ha", "Hall", "2.4", "1.6", "12.8", "1.6", "1.6", "1.6", "0.50", "Li", "2"}};

String [] rooms = getroomNAMES(roomsIN);
String [][] roomDATA=getroomDATA(rooms, roomDATAIN);
int [] lockgenes = getroomLOCKS(rooms, lockIN);

public void setup() {
  //size(640, 360,p3d);
  
  orientation(LANDSCAPE);
  scand = displayDensity;
  inandroid = true;
  setupgr();
  setupho();
  setuptr();
  setupid();
  setupdraw();
  setupsl(rooms, roomDATA);
  changeroomDATA();
  setupga();
  setupdz();
  
}
public void draw() {
  background(255);
  drawgr();
  drawho();
  drawsl();
  
  if (evolve) {
    for (int i=0; i<10; i++) {
      ho.evolvehoga(); 
      ho.ng = ho.clonengs(5);
      ho.setuptr(lockgenes);
      ho.setupid();
      ho.setupdraw();
    }

  }
  drawdz();
}
public void mousePressed() {
    pressdz();
  presssl();
  pressadddrooms();
  pressho();

}
public void mouseReleased() {
  releasesl();
}
public void keyPressed() {
  typeho();
  if (key == 'l') {
    ho.insertbgenes(lockgenes);
    ho.insertgenes(ranflarr(ho.ngenes, 0, 1));
    setuptr();
    setupid();
    setupdraw();
  }
  if (key == 'p') {
    rooms = addtostarr(rooms, "Ki");
    roomDATA=getroomDATA(rooms, roomDATAIN);
    lockgenes = getroomLOCKS(rooms, lockIN);
    //setupsl(rooms, roomDATA);
    setupho();
    setuptr();
    setupid();
    setupdraw();
  }
  //if (key == 'f') {
  //  setupho2();
  //}
  //if (key=='e') {
  //  evolve = !evolve;
  //}
}
House ho;

public void setupho() {
  ArrayList <Zone> homedges= new ArrayList <Zone> ();
  homedges.add(new Zone (new PVector(0, 0,0), new PVector(1, 1,0)));
  //homedges.add(new Zone (new PVector(0, 0), new PVector(.5, 1)));
  //homedges.add(new Zone (new PVector(.5, .1), new PVector(1, .9)));
  ho = new House(rooms, homedges);
}

public void setuphoinedges(ArrayList <Zone> homedges){
    ho = new House(rooms, homedges);
}
public void setuptr() {
  ho.setuptr(lockgenes);
}
public void setupid() {
  ho.setupid();
}
public void setupdraw() {
  ho.setupdraw();
}
public void setupga() {
  ho.setuphoga();
}
public void drawho() {
  //ho.displaytree();
  
  if (!dr.state) ho.displayhouse();
   if (!dr.state) scoreshow(new PVector(width*.75f-(50*scand), 20*scand), new PVector (100*scand, 10*scand), PApplet.parseInt((10-ho.totfit)*10), 150, 255);
}
public void pressho() {
  ho.press();
}
public void typeho() {
  ho.type();
}
class House {
  String [] rooms;
  float permgene, locgene[], subgene[], totfit;
  ArrayList <Node> no;
  int nrooms, ngenes;
  PVector treesize = new PVector(180*scand, 30*scand);
  PVector treepos = new PVector(600*scand, 250*scand);
  ArrayList <Zone> homedges;
  Nodegene [] ng;
  Population p;
  Atribute [] at;
  House(String [] _rooms, ArrayList <Zone> _homedges) {
    rooms = _rooms;
    nrooms = rooms.length;
    ngenes =min0(nrooms-2)+1+min0(nrooms-1);
    locgene= new float [min0(rooms.length-2)];
    subgene= new float [min0(rooms.length-1)];
    homedges=_homedges;
    ng = new Nodegene [ngenes];
    for (int i=getngnum("lmin", nrooms); i<getngnum("lmax", nrooms); i++) ng[i] = new Nodegene ("l", 0, false, new PVector(treepos.x-treesize.x, treepos.y+i*15*scand));
    for (int i=getngnum("pmin", nrooms); i<getngnum("pmax", nrooms); i++) ng[i] = new Nodegene ("p", 0, false, new PVector(treepos.x-treesize.x, treepos.y+i*15*scand));
    for (int i=getngnum("smin", nrooms); i<getngnum("smax", nrooms); i++) ng[i] = new Nodegene ("s", 0, false, new PVector(treepos.x-treesize.x, treepos.y+i*15*scand));
    insertgenes(ranflarr(ngenes, 0, 1));
  }
  public House clonenewgenes(float [] genes) {        
    House clone = new House(clonestarr(rooms), clonezonelist(homedges));
    clone.ng = clonengarrwithgenes(ng, genes);
    clone.setuptr(lockgenes);
    clone.setupid();
    clone.setupdraw();
    return clone;
  }
  public void setuptr(int [] lockgenes) {
    insertbgenes(lockgenes);
    calctree();
    calcrela();
  }
  public void setupid() {
    calcideal();
    calchouse();
    strechzones();
    calcborders();
    calcreals();
    calcsubparamminmax();
    calchouse();
    strechzones();
    calcborders();
    calcreals();
    calcfit();
  }
  public void setupdraw() {
    calcbordertypes();
    calcmuebles();
  }
  public void insertgenes(float geneins[]) {   
    if (geneins.length<ng.length)  for (int i=0; i<geneins.length; i++) if (!ng[i].inactive) ng[i].value = geneins[i];
    if (geneins.length>=ng.length) for (int i=0; i<ng.length; i++)      if (!ng[i].inactive) ng[i].value = geneins[i];
  }
  public void insertbgenes(int genebins[]) { 
    if (genebins.length<ng.length)  for (int i=0; i<genebins.length; i++) ng[i].inactive = PApplet.parseBoolean(genebins[i]);
    if (genebins.length>=ng.length) for (int i=0; i<ng.length; i++)       ng[i].inactive = PApplet.parseBoolean(genebins[i]);
  } 
  public void calctree() {
    no = nodecrea(rooms, ngvalues(ng, getngnum("lmin", nrooms), getngnum("lmax", nrooms)));
    for (int n=0; n<no.size(); n++) if (no.get(n).isleaf) no.get(n).code = permutation01(rooms, ngvalue(ng, getngnum("pmin", nrooms)))[no.get(n).leafi];
    for (int n=0; n<no.size(); n++) if (!no.get(n).isleaf) no.get(n).subparam[0] = roundit(ngvalue(ng, no.get(n).inneri+getngnum("smin", nrooms)), 2)+"";
    for (int n=0; n<no.size(); n++) if (!no.get(n).isleaf) ng[no.get(n).inneri+getngnum("smin", nrooms)].loc = no.get(n).loc;
    for (int n=0; n<no.size(); n++) no.get(n).ischilda = ifischilda(no.get(n).loc);
    for (int n=0; n<no.size(); n++) no.get(n).calcnodepos(treepos, treesize);
  }
  public void calcrela() {
    for (int n=0; n<no.size(); n++) no.get(n).father = calcrelative(no.get(n), no, "father");
    for (int n=0; n<no.size(); n++) no.get(n).childa = calcrelative(no.get(n), no, "childa");
    for (int n=0; n<no.size(); n++) no.get(n).childb = calcrelative(no.get(n), no, "childb");
    for (int n=0; n<no.size(); n++) no.get(n).brother =calcrelative(no.get(n), no, "brother");
  }
  public void calcideal() {
    for (int n=0; n<no.size(); n++) if (no.get(n).isleaf) for (int i=0; i<no.get(n).ideals.length; i++) no.get(n).ideals[i] = roomideals(no.get(n).code, i, roomDATA);
    for (int n=no.size()-1; n>=0; n--) if (!no.get(n).isleaf) no.get(n).ideals[2] = (PApplet.parseFloat(no.get(n).childa.ideals[2])+PApplet.parseFloat(no.get(n).childb.ideals[2]))+"";
    for (int n=0; n<no.size(); n++) if (no.get(n).father!=null) no.get(n).subparam[1] = roundit(PApplet.parseFloat(no.get(n).ideals[2])/PApplet.parseFloat(no.get(n).father.ideals[2]), 2)+"";
  }
  public void calchouse() {
    no = calcallzones(no, homedges);
  }
  public void calcborders() {
    for (int n=0; n<no.size(); n++) for (int z=0; z<no.get(n).zones.size(); z++) no.get(n).zones.get(z).borders = borderDEzones(no.get(n).zones.get(z), no);
  }
  public void calcreals() { //0 :code 1:name 2: area 3:minW
    for (int n=0; n<no.size(); n++) if (no.get(n).isleaf) for (int z=0; z<no.get(n).zones.size(); z++) no.get(n).reals[2] = nf(no.get(n).zones.get(z).area(), 1, 2)+"";
    for (int n=0; n<no.size(); n++) if (no.get(n).isleaf) if (no.get(n).zones.size()>0) no.get(n).reals[6] = nf(no.get(n).zones.get(0).zwidth(), 1, 2)+"";
    for (int n=0; n<no.size(); n++) if (no.get(n).isleaf) if (no.get(n).zones.size()>0) no.get(n).reals[7] = nf(no.get(n).zones.get(0).zwidth(), 1, 2)+"";
    for (int n=0; n<no.size(); n++) if (no.get(n).isleaf) for (int z=0; z<no.get(n).zones.size(); z++) for (int b=0; b<no.get(n).zones.get(z).borders.size(); b++) no.get(n).zones.get(z).borders.get(b).adj = caldadjborder(no.get(n).zones.get(z).borders.get(b), no);
    for (int n=0; n<no.size(); n++) if (no.get(n).isleaf) no.get(n).reals[9]=adjlist(no, no.get(n));
    for (int n=0; n<no.size(); n++) if (no.get(n).isleaf) if (no.get(n).zones.size()>0) no.get(n).reals[8] = nf(no.get(n).zones.get(0).prop(), 1, 2)+"";
  }
  public void strechzones() {
    scaleno(no, calcstrechfactor(no));
    gridno(no);
  }
  public void calcbordertypes() {
    for (int n=0; n<no.size(); n++) if (no.get(n).isleaf) for (int z=0; z<no.get(n).zones.size(); z++) for (int b=0; b<no.get(n).zones.get(z).borders.size(); b++) no.get(n).zones.get(z).borders.get(b).id[4] = calcbordertype(no, no.get(n), no.get(n).zones.get(z).borders.get(b));                                                         //entry
    for (int n=0; n<no.size(); n++) if (no.get(n).isleaf) calcbordertyperoom(no, no.get(n));
  }
  public void calcmuebles() {
    for (int n=0; n<no.size(); n++) if (no.get(n).isleaf) if (no.get(n).zones.size()>0) no.get(n).muebles = calcmuebleslist((no.get(n)));
  }
  public void recalc0zones() {
    for (int n=0; n<no.size(); n++) if (no.get(n).isleaf)  if (no.get(n).zones.size()>0)  if (no.get(n).zones.get(0).xdim()==0) if (ischildx(PApplet.parseFloat(no.get(n).father.subparam[0]))) {
      float newper = grunit/no.get(n).father.zones.get(0).xdim();
      no.get(n).subparam[1] = newper + "";
      no.get(n).brother.subparam[1] = (1-newper)+"";
    }
    for (int n=0; n<no.size(); n++) if (no.get(n).isleaf)  if (no.get(n).zones.size()>0)  if (no.get(n).zones.get(0).ydim()==0) if (!ischildx(PApplet.parseFloat(no.get(n).father.subparam[0]))) {
      float newper = grunit/no.get(n).father.zones.get(0).ydim();
      no.get(n).subparam[1] = newper + "";
      no.get(n).brother.subparam[1] = (1-newper)+"";
    }
  }
  public void calcsubparamminmax() {
    for (int n=0; n<no.size(); n++) if (no.get(n).isleaf) if (no.get(n).zones.size()>0)   if (PApplet.parseFloat(no.get(n).ideals[7])<PApplet.parseFloat(no.get(n).reals[7])||(PApplet.parseFloat(no.get(n).ideals[6])>PApplet.parseFloat(no.get(n).reals[6]))) {
      boolean childx = ischildx(PApplet.parseFloat(no.get(n).father.subparam[0]));
      boolean lengthisx = no.get(n).zones.get(0).lengthisx();  
      if ((childx&&!lengthisx)||(!childx&&lengthisx)) {
        float newper = roundit(PApplet.parseFloat(no.get(n).ideals[6])/no.get(n).father.zones.get(0).xdim(), 2);
        no.get(n).subparam[1] = newper + "";
        no.get(n).brother.subparam[1] = (1-newper)+"";
      }
    }
  }
  public void calcfit() {
    totfit=0;
    totfit += calctotpropfit(no, 5);
    totfit += calctotadjfit(no, 50);
    totfit +=calcmissingzonesfit(no, 300);
    totfit+=calcclosetinbetween(no, 1000);
  }
  public void displaytree() {
    for (int n=0; n<ng.length; n++) ng[n].display();
    for (int n=0; n<no.size(); n++) no.get(n).displaynodetree();
    fill(255, 0, 0);
    text(totfit, treepos.x, treepos.y-20);
  }
  public void displayhouse() {
    //for (int h=0; h<homedges.size(); h++) homedges.get(h).displaycolor();
    for (int n=0; n<no.size(); n++) no.get(n).displaymuebles();
    for (int n=0; n<no.size(); n++) no.get(n).displayrooms();
  }

  public void press() {
    for (int i=0; i<ng.length; i++) ng[i].press();
  }
  public void type() {
    if (key=='q') for (Nodegene n : ng) n.scroll(true);
    if (key=='w') for (Nodegene n : ng)  n.scroll(false);
    if (key=='a') for (Nodegene n : ng)  n.activateit(true);
    if (key=='q'||key=='w') setuptr(lockgenes);
    if (key=='q'||key=='w') setupid();
  }
  public void setuphoga() {
    at = new Atribute [ngenes];           
    for (int i=0; i<at.length; i++) at[i] = new Atribute ("g"+i, 0, 1);
    int nPopX= 10;  //25
    int nPopY = 10;  //25
    int popSize = nPopX*nPopY;
    p = new Population(this, popSize, at);
  }
  public void evolvehoga() {
    p.evolve(this);
  }
  public void drawho1ga(int ind) {
    House temph  = this.clonenewgenes(p.pop[ind].phenos);
    temph.displayhouse();
  }
  public Nodegene [] clonengs(int ind) {
    House temph = this.clonenewgenes(p.pop[ind].phenos);
    return temph.ng;
  }
}
public String [] roomcodedeDATAIN(String[][] roomDATA) {
  String stout [] = new String [roomDATA.length];
  for (int i=0; i<stout.length; i++) stout[i] = roomDATA[i][0];
  return stout;
}

public String [] roomsSINroom(String[] roomsIN, int roomi) {
  String stout [] = new String [roomDATA.length];
  ArrayList <String> sto = new ArrayList <String>();
  for (int i=0; i<roomsIN.length; i++) {
    if (i!=roomi) sto.add(roomsIN[i]);
    else sto.add("");
  }  
  for (int i=0; i<sto.size(); i++) stout[i] = sto.get(i);
  return stout;
}
public int roomidealadj(String [] rooms, String rcode, String[][] roomDATA, int coladj) {
  int iout =0;
  for (int i=0; i<rooms.length; i++) if (txequal(rooms[i], roomideals(rcode, coladj, roomDATA))) iout = i;
  return iout;
}

public String roomideals (String rcode, int idealnum, String[][] roomDATA) {
  String stout = "0";
  boolean rfound = false; 
  for (int i=0; i<roomDATA.length; i++) if (txequal(roomnonumber(rcode), roomDATA[i][0])) {
    stout = roomDATA[i][idealnum]; 
    rfound = true;
  }
  if (!rfound) println("room not found");
  return stout;
}
public String [] roomidealsall (String rcode, String[][] roomDATA) {
  String stout [] = new String [roomDATA[0].length];
  boolean rfound = false; 
  for (int i=0; i<roomDATA.length; i++) if (txequal(roomnonumber(rcode), roomDATA[i][0])) {
    stout = roomDATA[i]; 
    rfound = true;
  }
  if (!rfound) println("room not found");
  return stout;
}
public String roomnonumber(String rcode){
  String stout = "";
  for (int i=0; i<rcode.length();i++) if (!Character.isDigit(rcode.charAt(i))) stout+=rcode.charAt(i);
  return stout;
}

public String [] getroomNAMES(String [] rooms) { 
  String [] roomDATAout = new String [rooms.length];
  for (int i=0; i<rooms.length; i++) {
  roomDATAout [i] = rooms[i];
  int count = 0;
  for (int j=0; j<i; j++) if (txequal(rooms[i],rooms[j])) count++;
  if (count>0) roomDATAout [i] = rooms[i]+(count+1);   
  }
  return roomDATAout;
}
public String [][] getroomDATA(String [] rooms, String [][] roomDATAin) { 
  String [][] roomDATAout = new String [rooms.length][];
  for (int i=0; i<rooms.length; i++) roomDATAout [i] = roomidealsall(rooms[i], roomDATAin);
  return roomDATAout;
}
public float min0max1roomDATA(int cols, String [][] roomDATAin, int min0max1) {
  float minmaxf = PApplet.parseFloat (roomDATAin[0][cols]); 
  if (min0max1==0)for (int i=0; i<roomDATAin.length; i++) if (PApplet.parseFloat(roomDATAin[i][cols])<minmaxf) minmaxf = PApplet.parseFloat(roomDATAin[i][cols]);
  if (min0max1==1) for (int i=0; i<roomDATAin.length; i++) if (PApplet.parseFloat(roomDATAin[i][cols])>minmaxf) minmaxf = PApplet.parseFloat(roomDATAin[i][cols]);
  return minmaxf;
}

public int [] getroomLOCKS(String [] rooms, int [] bin) { 
  int [] iout = new int [1+min0(rooms.length-2)+min0(rooms.length-1)];  
  for (int i=0; i<bin.length; i++) if (i<rooms.length) iout [i] = bin [i];
  return iout;
}


public void scoreshow(PVector pos, PVector size, float inscore, int coloron, int coloroff) {
  pushStyle();
  if (inscore<0) inscore = 0;
  stroke(150);
  fill(coloroff);
  rect(pos.x, pos.y, size.x, size.y, size.y/2);
  fill(coloron);
  rect(pos.x, pos.y, size.x*(inscore/100), size.y, size.y/2);
  fill(150);
  textAlign(CENTER, BOTTOM);
  text("score:"+PApplet.parseInt(inscore)+"%", pos.x+size.x*.5f, pos.y); 
  popStyle();
}
class Node {
  String code, loc;
  int nodei, leafi, inneri;
  PVector pos, size;
  boolean isleaf, ischilda;
  String ideals[], reals [], subparam [];
  ArrayList <Zone>  zones;
  Node father, childa, childb, brother;
  ArrayList <Mueble> muebles;
  Node(int _nodei, String _loc, boolean _isleaf) {
    nodei=_nodei;
    loc = _loc;
    isleaf = _isleaf;
    size = new PVector (15*scand, 15*scand);
    ideals = new String [roomDATA[0].length];
    reals = new String [roomDATA[0].length];
    subparam = new String [2]; // 0:X or Y 1: Percentage subdivition
  }
  public void calcnodepos( PVector _treepos, PVector _treesize) {
    pos = locpos(loc, _treepos, _treesize);
  }
  public void displaynodetree() {
    pushStyle();
    rectMode(CENTER);
    textAlign(CENTER, CENTER);
    stroke(200);
    noFill();
    rect(pos.x, pos.y, size.x, size.y, size.y/4.0f);
    fill(0);
    textSize(8);
    if (code!=null) text(code, pos.x, pos.y);
    if (father!=null) line(pos.x, pos.y-size.y/2, father.pos.x, father.pos.y+size.y/2);
    if (isleaf) if (ideals!=null) for (int i=0; i<ideals.length; i++) if (ideals[i]!=null)  text(ideals[i], pos.x-10, pos.y+10+10*i);
    if (isleaf) if (reals!=null ) for (int i=0; i<reals.length; i++) if (reals[i]!=null)  text(reals[i], pos.x+10, pos.y+10+10*i);
    //if (subparam!=null) for (int i=0; i<subparam.length; i++) if (subparam[i]!=null)  text(subparam[i], pos.x+30*i, pos.y+30);
    popStyle();
  }
  public void displayrooms() {
    //if (isleaf) for (int z=0; z<zones.size(); z++)  zones.get(z).displaycolor(code);
    // if (isleaf) for (int z=0; z<zones.size(); z++)  zones.get(z).displaypoints();
    //if (isleaf) if (zones!=null) if (zones.size()>0) if (entry!=null)  if (entry.zones!=null) if (entry.zones.size()>0) daline(scgr(zones.get(0).pmid), scgr(entry.zones.get(0).pmid), 10*scand);
    if (isleaf) if (zones!=null) for (int z=0; z<zones.size(); z++) if (zones.get(z).borders!=null) for (int b=0; b<zones.get(z).borders.size(); b++) zones.get(z).borders.get(b).display();
    if (isleaf) if (zones!=null) if (zones.size()>0) zones.get(0).displaytittle(code);
  }
  public void displaymuebles() {
    if (isleaf) if (muebles!=null) for (Mueble m : muebles)   m.display();
  }
}
public ArrayList <Node> nodecrea(String []rooms, float [] locgene) {
  ArrayList <Node> nout = new ArrayList <Node> ();
  int nodei=0;
  if (rooms.length==1) nout.add(new Node(nodei, "0", true));
  else {
    for (int i=0; i<locgene.length+3; i++) {
      if (i==0) nout.add(new Node(nodei, "0", false));
      else if (i==1) nout.add(new Node(nodei, "00", true));
      else if (i==2) nout.add(new Node(nodei, "01", true));
      if (i==0||i==1||i==2) nodei++;
      else {
        ArrayList <Node> leafs = new ArrayList <Node> ();
        for (int j=0; j<nout.size(); j++) if (nout.get(j).isleaf) leafs.add(nout.get(j));
        int leafsel = leafs.get(leafs.size()-1).nodei;
        if (locgene[i-3]<1) leafsel = leafs.get(PApplet.parseInt(map(locgene[i-3], 0, 1, 0, leafs.size()))).nodei;
        nout.get(leafsel).isleaf = false;
        nout.add(new Node(nodei, nout.get(leafsel).loc+"0", true));
        nout.add(new Node(nodei+1, nout.get(leafsel).loc+"1", true));
        nodei+=2;
      }
    }
    int leafcount=0;
    for (int i=0; i<nout.size(); i++) if (nout.get(i).isleaf) {
      nout.get(i).leafi=leafcount;
      leafcount++;
    }
    int innercount=0;
    for (int i=0; i<nout.size(); i++) if (!nout.get(i).isleaf) {
      nout.get(i).inneri=innercount;
      innercount++;
    }
    for (int i=0; i<nout.size(); i++) if (!nout.get(i).isleaf) nout.get(i).leafi=-1;
    for (int i=0; i<nout.size(); i++) if (nout.get(i).isleaf) nout.get(i).inneri=-1;
  }
  return nout;
}
class Nodegene {
  String type, loc;
  float value, step, min, max;
  PVector  pos;
  boolean select, inactive;
  PVector size = new PVector (15*scand, 15*scand);
  Nodegene(String _type, float _value, boolean _inactive, PVector _pos) {
    type = _type;
    value=roundit(_value, 2);
    inactive=_inactive;
    step=.01f;
    min=0;
    max=1;
    pos=_pos;
  }
  public Nodegene cloneit() {
    Nodegene nout = new Nodegene (type, value, inactive, pos.copy());
    return nout;
  }
  public Nodegene cloneactgene(float genevalue) {
    Nodegene nout;
    if (inactive) nout = new Nodegene (type, value, inactive, pos.copy());
    else nout = new Nodegene (type, genevalue, inactive, pos.copy());
    return nout;
  }
  public void display() {
    pushStyle();
    rectMode(CENTER);
    textAlign(CENTER, CENTER);
    fill(255);
    stroke(200);
    if (select)   fill(200);
    if (inactive&&select) fill (255, 0, 0, 100);
    if (inactive&&!select) fill (255, 0, 0, 50);
    if (!inactive&&select) fill (0, 255, 0, 100);
    if (!inactive&&!select) fill (0, 255, 0, 50);
    rect(pos.x, pos.y, size.x, size.y, size.y*.25f);
    fill(100);
    text(nf(value, 0, 2), pos.x, pos.y);
    text(type, pos.x-15, pos.y);
    popStyle();
  }
  public boolean isover() {
    if (mouseX>pos.x-(size.x/2.0f)&&mouseX<pos.x+(size.x/2.0f)&&mouseY>pos.y-(size.y/2.0f)&&mouseY<pos.y+(size.y/2.0f)) return true;
    else return false;
  }
  public void press() {
    if (isover()) select =!select;
  }
  public void scroll(boolean updown) {
    if (select&&!updown&&value<max) value = roundit(value + step, 4);
    if (select&&updown&&value>min)  value = roundit(value - step, 4);
    if (value<min) value=roundit(min, 2);
    if (value>max) value=roundit(max, 2);
  }
  public void changeval(float newval) {
    if (!inactive) value = newval;
  }
  public void activateit(boolean act) {
    if (act&&select) inactive = !inactive;
  }
}
public float [] ngvalues (Nodegene [] ng, int iniv, int endv) {
  float fout [] = new float [endv-iniv];
  for (int i=0; i<endv-iniv; i++) fout [i] = ng [i+iniv].value;
  return fout;
}
public float  ngvalue (Nodegene [] ng, int iniv) {
  float fout  = ng [iniv].value;
  return fout;
}
class Zone {
  int     id [] = new int     [2];
  PVector pt [] = new PVector [4];
  ArrayList <Border> borders;

  Zone (PVector _p00, PVector _p11) {
    pt[0] = _p00;
    pt[1] =_p11;
    pt[2] = new PVector (pt[0].x, pt[1].y);
    pt[3] = new PVector (pt[1].x, pt[0].y);
  }
  public Zone cloneit() {
    Zone zout = new Zone (pt[0].copy(), pt[1].copy());
    zout.id = cloneintarr(id);
    zout.borders = cloneborderslist(borders);
    return zout;
  }
  public Zone clonenewpts(PVector newp10, PVector newp11) {
    Zone zout = new Zone (newp10, newp11);
    zout.id = cloneintarr(id);
    zout.borders = cloneborderslist(borders);
    return zout;
  }
  public PVector pmid() {
    return new PVector((pt[0].x+pt[1].x)*.5f, (pt[0].y+pt[1].y)*.5f);
  }
  public float area () {
    return  abs(pt[1].x-pt[0].x)*abs(pt[1].y-pt[0].y);
  }
  public float prop () {
    if ((pt[1].x-pt[0].x)<(pt[1].y-pt[0].y)) return ((pt[1].x-pt[0].x)/(pt[1].y-pt[0].y));
    else return ((pt[1].y-pt[0].y)/(pt[1].x-pt[0].x));
  }
  public float xdim() {
    return abs(pt[1].x-pt[0].x);
  }
  public float ydim() {
    return abs(pt[1].y-pt[0].y);
  }
  public boolean lengthisx() {
    boolean bout = xdim()>=ydim();
    return bout;
  }
  public float zwidth() {
    if (!lengthisx()) return xdim();
    else return ydim();
  }
  public float zlength() {
    if (lengthisx()) return xdim();
    else return ydim();
  }
  public void displaypoints() {
    noStroke();
    fill(255, 0, 0, 100);
    ellipse(scgr(pt[0]).x, scgr(pt[0]).y, 5, 5);
    ellipse(scgr(pt[3]).x, scgr(pt[3]).y, 5, 5);
    ellipse(scgr(pt[2]).x, scgr(pt[2]).y, 5, 5);
    ellipse(scgr(pt[1]).x, scgr(pt[1]).y, 5, 5);
  }
  public void displaytittle(String code) {
    pushStyle();
    textAlign(CENTER, CENTER);
    stroke(150);
    fill(255);
    if (code.length()<=2)  textSize(12*scand);
    else textSize(10*scand);
    ellipse(scgr(pmid()).x, scgr(pmid()).y, 20*scand, 20*scand);

    fill(150);
    int adjcount = 0;
    for (Border b : borders) if (b.adj!=null) if (b.adj.id[0]!=b.id[0]) adjcount++;
    if (code!=null)  text(code, scgr(pmid()).x, scgr(pmid()).y);
    popStyle();
  }
  public void displaycolor(String code) {
    pushStyle();
    rectMode(CORNERS);
    colorDEcode(code, 50, roomDATA);
    rect(scgr(pt[0]).x, scgr(pt[0]).y, scgr(pt[1]).x, scgr(pt[1]).y);
    popStyle();
  }
  public void displaycreate() {
    pushStyle();
    strokeWeight(2*scand);
    stroke(150);
    strokeWeight(1*scand);
    stroke(180);
    hashrectangle(pt[0].x, pt[0].y, pt[1].x, pt[1].y);
    popStyle();
  }
}
public ArrayList <Zone> calczones(Node n, ArrayList <Zone> homedge) {
  ArrayList <Zone> zo = new ArrayList <Zone> ();
  if (n.father==null) zo=homedge;
  else {
    Node father = n.father;
    ArrayList <Zone> fzones = father.zones;
    boolean child1 = n.ischilda;
    boolean childx = ischildx(PApplet.parseFloat(n.father.subparam[0]));
    float subper = PApplet.parseFloat(n.subparam[1]);
    float brosubper = PApplet.parseFloat(n.brother.subparam[1]);
    float p00min = pminmax(0, fzones, childx);
    float p11max = pminmax(1, fzones, childx);
    float pline0, pline1;
    if (child1) pline0 = map (subper, 0, 1, p00min, p11max);
    else pline0 = map (brosubper, 0, 1, p00min, p11max);
    if (child1)  pline1 = map (subper, 0, 1, p00min, p11max);
    else pline1 = map (brosubper, 0, 1, p00min, p11max);
    for (Zone f : fzones) if (zonewithinline(f, pline0, pline1, child1, childx)) zo.add(breakfzones(f, pline0, pline1, child1, childx));
  }
  if (zo.size()>0) zo = sortzonesbyarea(zo);
  for (int i=0; i<zo.size(); i++) zo.get(i).id[0] = n.nodei;
  for (int i=0; i<zo.size(); i++) zo.get(i).id[1] = i;
  return zo;
}
public ArrayList <Node> calcallzones(ArrayList <Node> no, ArrayList <Zone> homedges) {
  for (int n=0; n<no.size(); n++) no.get(n).zones = calczones(no.get(n), homedges);
  return no;
}
public ArrayList <Zone> prophomedges(ArrayList <Zone> homedge, float scaleprop) {
  ArrayList <Zone> newhomedge = new ArrayList <Zone> ();
  for (int i=0; i<homedge.size(); i++) newhomedge.add(new Zone(new PVector (homedge.get(i).pt[0].x*scaleprop, homedge.get(i).pt[0].y), new PVector (homedge.get(i).pt[1].x*scaleprop, homedge.get(i).pt[1].y)));
  return newhomedge;
}
public float calctotpropfit(ArrayList <Node> no, float fitperprop) { 
  float   totpropfit = 0;
  for (int n=0; n<no.size(); n++) if (no.get(n).isleaf) if (no.get(n).zones.size()>0) totpropfit+=abs(PApplet.parseFloat(no.get(n).ideals[8])-PApplet.parseFloat(no.get(n).reals[8]))*fitperprop;
  return totpropfit;
}
public float calctotadjfit(ArrayList <Node> no, float fitpernonadj) {  
  float   adjfit = 0;
  for (int n=0; n<no.size(); n++) if (no.get(n).isleaf) {
    String idealsep = no.get(n).ideals[9];
    boolean roominhome = false;
    boolean isalreadyadj = false;
    for (int m=0; m<no.size(); m++) if (txequal(idealsep, no.get(m).code)) roominhome = true;
    if (!roominhome) isalreadyadj = true; 
    String [] realsep = split(no.get(n).reals[9], ' ');
    for (int j=0; j<realsep.length; j++) if (txequal(realsep[j], idealsep)||txequal(idealsep, "")) isalreadyadj = true;
    if (!isalreadyadj) adjfit +=fitpernonadj;
  }
  return adjfit;
}
public float calcmissingzonesfit(ArrayList <Node> no, float fitpermis) {
  float   missfit = 0;
  for (int n=0; n<no.size(); n++) if (no.get(n).isleaf) if (no.get(n).zones.size()==0) missfit +=fitpermis;
  for (int n=0; n<no.size(); n++) if (no.get(n).isleaf) if (no.get(n).zones.size()>0) if (no.get(n).zones.get(0).area()==0) missfit +=fitpermis;
  return missfit;
}

public float calcclosetinbetween(ArrayList <Node> no, float fitpermis) {
  float   missfit = 0;
  for (int n=0; n<no.size(); n++) if (no.get(n).isleaf) if (txequal(no.get(n).ideals[10], "2")) if (!no.get(n).brother.isleaf) missfit +=fitpermis;
  return missfit;
}
int grayMue = color(152, 152, 152);
int blackLet= color(75, 75, 75);
int blackWall = color(30, 30, 30);
int blueWindow = color(152, 180, 222);

class Border {
  int     id [] = new int    [5];
  PVector pt [] = new PVector[2];
  Border adj;
  Border(int [] _id, PVector [] _pt) {
    id = _id;
    pt=_pt;
  }
  public Border cloneit() {
    Border bout = new Border (cloneintarr(id), clonevecarr(pt));
    return bout;
  }
  public PVector pmid(){
    return new PVector ((pt[0].x+pt[1].x)*.5f, (pt[0].y+pt[1].y)*.5f);
  }
  public float bsize(){
    return PVector.dist(pt[1], pt[0]);
  }
  public void display() {
    pushStyle();
    drawwindow(id[4], pt[0], pt[1]);
    drawdoor(id[4], id[2], pt[0], pt[1]);
    drawwall(id[4], adj, pt[0], pt[1]);
    drawopening(id[4], adj, pt[0], pt[1]);
    drawsuportfurniture(id[4], adj, pt[0], pt[1]);
    popStyle();
  }
}
public Border caldadjborder(Border bord, ArrayList <Node> nodes) {
  Border bout=null;
  for (int n=0; n<nodes.size(); n++) if (nodes.get(n).isleaf) for (int z=0; z<nodes.get(n).zones.size(); z++)     for (int b=0; b<nodes.get(n).zones.get(z).borders.size(); b++) {
    Border bord2 = nodes.get(n).zones.get(z).borders.get(b);
    if (!(bord.id[0]==bord2.id[0]&&bord.id[1]==bord2.id[1]&&bord.id[3]==bord2.id[3])) {
      if (vectorsequal(bord.pt[0], bord2.pt[0])&&vectorsequal(bord.pt[1], bord2.pt[1])) bout=nodes.get(n).zones.get(z).borders.get(b);
    }
  }
  return bout;
}
public ArrayList<Border> borderDEzones(Zone zone, ArrayList <Node> nodes) {
  ArrayList <Border> bout = new ArrayList <Border> (); 
  PVector [][] ps = new PVector [4][2];
  ps[0][0] = zone.pt[0].copy();
  ps[0][1] = zone.pt[2].copy();
  ps[1][0] = zone.pt[2].copy();
  ps[1][1] = zone.pt[1].copy();
  ps[2][0] = zone.pt[3].copy();
  ps[2][1] = zone.pt[1].copy();
  ps[3][0] = zone.pt[0].copy();
  ps[3][1] = zone.pt[3].copy();
  boolean [] isonx = {true, false, true, false};
  for (int s=0; s<4; s++) {
    ArrayList <PVector> pointsinside = new ArrayList <PVector> (); 
    pointsinside.add(ps[s][0]);
    for (int n=0; n<nodes.size(); n++) if (nodes.get(n).isleaf) for (int z=0; z<nodes.get(n).zones.size(); z++) {
      PVector p00 = nodes.get(n).zones.get(z).pt[0].copy();
      PVector p01 = nodes.get(n).zones.get(z).pt[2].copy();
      PVector p10 = nodes.get(n).zones.get(z).pt[3].copy();
      PVector p11 = nodes.get(n).zones.get(z).pt[1].copy();
      if (sameXY(ps[s][0], p00, isonx[s])&&withinXY(ps[s][0], ps[s][1], p00, !isonx[s])) pointsinside.add(nodes.get(n).zones.get(z).pt[0]);    
      if (sameXY(ps[s][0], p01, isonx[s])&&withinXY(ps[s][0], ps[s][1], p01, !isonx[s])) pointsinside.add(nodes.get(n).zones.get(z).pt[2]);    
      if (sameXY(ps[s][0], p10, isonx[s])&&withinXY(ps[s][0], ps[s][1], p10, !isonx[s])) pointsinside.add(nodes.get(n).zones.get(z).pt[3]);    
      if (sameXY(ps[s][0], p11, isonx[s])&&withinXY(ps[s][0], ps[s][1], p11, !isonx[s])) pointsinside.add(nodes.get(n).zones.get(z).pt[1]);
    }
    pointsinside.add(ps[s][1]);
    for (int i=0; i<pointsinside.size()-1; i++) if (!vectorsequal(pointsinside.get(i), pointsinside.get(i+1))) {
      int ids [] = new int [5];
      ids[0] = zone.id[0];
      ids[1] = zone.id[1];
      ids[2] = s;
      PVector pts [] = new PVector [2];
      pts[0] = pointsinside.get(i);
      pts[1] = pointsinside.get(i+1);
      bout.add(new Border(ids, pts));
    }
  }
  for (int b=0; b<bout.size(); b++) bout.get(b).id[3] = b;
  return bout;
}
public void drawopening(int tipo, Border adj, PVector p0, PVector p1) {
  if (tipo==4) {
    strokeWeight(2*scand);
    stroke(grayMue);
    line( scgr(p0).x, scgr(p0).y, scgr(p1).x, scgr(p1).y);
  }
}
public void drawsuportfurniture(int tipo, Border adj, PVector p0, PVector p1) {
  boolean drawwall = false;
  if (adj!=null) if (tipo==8&&adj.id[4]!=4&&adj.id[4]!=5)drawwall = true;
  if (adj==null) if (tipo==8) drawwall = true;
  if (drawwall) {
    strokeWeight(4*scand);
    stroke(blackWall);
    line( scgr(p0).x, scgr(p0).y, scgr(p1).x, scgr(p1).y);
    //stroke(255, 0, 0);
    //ellipse( (scgr(p0).x+scgr(p1).x)/2, (scgr(p0).y+scgr(p1).y)/2, 5, 5);
  }
}
public void drawwall(int tipo, Border adj, PVector p0, PVector p1) {
  boolean drawwall = false;
  if (adj!=null) if (tipo==7&&adj.id[4]!=4&&adj.id[4]!=5)drawwall = true;
  if (adj==null) if (tipo==7) drawwall = true;
  if (drawwall) {
    strokeWeight(4*scand);
    stroke(blackWall);
    line( scgr(p0).x, scgr(p0).y, scgr(p1).x, scgr(p1).y);
  }
}
public void drawdoor(int tipo, int sidei, PVector p0, PVector p1) {
  if (tipo==5) {
    noFill();
    stroke(blackWall);
    strokeWeight(1*scand);
    if (sidei==0) arc(scgr(p0).x, scgr(p0).y, scsif(.90f*2), scsif(.90f*2), 0, PI*.5f);
    if (sidei==1) arc(scgr(p0).x, scgr(p1).y, scsif(.90f*2), scsif(.90f*2), PI*1.5f, PI*2);
    if (sidei==2) arc(scgr(p1).x, scgr(p1).y, scsif(.90f*2), scsif(.90f*2), PI, PI*1.5f);
    if (sidei==3) arc(scgr(p1).x, scgr(p0).y, scsif(.90f*2), scsif(.90f*2), PI*.5f, PI);
    if (sidei==0) line(scgr(p0).x, scgr(p0).y, scgr(p0).x+scsif(.90f), scgr(p0).y);
    if (sidei==1) line(scgr(p0).x, scgr(p1).y, scgr(p0).x, scgr(p1).y-scsif(.90f));
    if (sidei==2) line(scgr(p1).x, scgr(p1).y, scgr(p1).x-scsif(.90f), scgr(p1).y);
    if (sidei==3) line(scgr(p1).x, scgr(p0).y, scgr(p1).x, scgr(p0).y+scsif(.90f));
    strokeWeight(2*scand);
    if (sidei==0) line(scgr(p0).x-5*scand, scgr(p0).y, scgr(p0).x+5*scand, scgr(p0).y);                        //marcos
    if (sidei==0) line(scgr(p0).x-5*scand, scgr(p0).y+scsif(.90f), scgr(p0).x+5*scand, scgr(p0).y+scsif(.90f));
    if (sidei==1) line(scgr(p0).x, scgr(p1).y-5*scand, scgr(p0).x, scgr(p1).y+5*scand);
    if (sidei==1) line(scgr(p0).x+scsif(.90f), scgr(p1).y-5*scand, scgr(p0).x+scsif(.90f), scgr(p1).y+5*scand);
    if (sidei==2) line(scgr(p1).x-5*scand, scgr(p1).y, scgr(p1).x+5*scand, scgr(p1).y);
    if (sidei==2) line(scgr(p1).x-5*scand, scgr(p1).y-scsif(.90f), scgr(p1).x+5*scand, scgr(p1).y-scsif(.90f));
    if (sidei==3) line(scgr(p1).x, scgr(p0).y-5*scand, scgr(p1).x, scgr(p0).y+5*scand);
    if (sidei==3) line(scgr(p1).x-scsif(.90f), scgr(p0).y-5*scand, scgr(p1).x-scsif(.90f), scgr(p0).y+5*scand);
    strokeWeight(4*scand);
    if (sidei==0) line(scgr(p0).x, scgr(p0).y+scsif(.9f), scgr(p1).x, scgr(p1).y);                              //pared
    if (sidei==1) line(scgr(p0).x+scsif(.9f), scgr(p1).y, scgr(p1).x, scgr(p1).y);
    if (sidei==2) line(scgr(p1).x, scgr(p1).y-scsif(.9f), scgr(p1).x, scgr(p0).y);
    if (sidei==3) line(scgr(p1).x-scsif(.9f), scgr(p0).y, scgr(p0).x, scgr(p0).y);
  }
}
public void drawwindow(int tipo, PVector p0, PVector p1) {
  if (tipo==6) {
    pushStyle();
    strokeWeight(2*scand);
    stroke(blueWindow);
    line( scgr(p0).x, scgr(p0).y, scgr(p1).x, scgr(p1).y);
    stroke(blackWall);
    if (p0.x==p1.x) {
      line(scgr(p0).x-5*scand, scgr(p0).y, scgr(p0).x+5*scand, scgr(p0).y);
      line(scgr(p1).x-5*scand, scgr(p1).y, scgr(p1).x+5*scand, scgr(p1).y);
    } else {
      line(scgr(p0).x, scgr(p0).y-5*scand, scgr(p0).x, scgr(p0).y+5*scand);
      line(scgr(p1).x, scgr(p1).y-5*scand, scgr(p1).x, scgr(p1).y+5*scand);
    }
    popStyle();
  }
}
class Mueble {
  int tipo;
  int rot;
  PVector pos, pos2;
  Mueble(  int _tipo, int _rot, PVector _pos, PVector _pos2) {
    tipo = _tipo;
    rot = _rot;
    pos=_pos;
    pos2=_pos2;
  }
  public Mueble cloneit() {
    Mueble cout = new Mueble(tipo, rot, pos, pos2);
    return cout;
  }
  public void display() {
    pushStyle();
    if (pos!=null&&pos2!=null) drawmueble(tipo, rot, pos, pos2);
    popStyle();
  }
}
public ArrayList <Mueble> calcmuebleslist(Node node) {
  ArrayList <Mueble> mout = new ArrayList <Mueble> ();
  if (txequal(roomnonumber(node.code), "Cl")) mout.add(new Mueble(7, 0, node.zones.get(0).pt[0], node.zones.get(0).pt[1]));
  if (txequal(roomnonumber(node.code), "Be")) for (Zone z : node.zones) for (Border b : z.borders) if (b.id[4]==8) if (z.zwidth()>2&&z.zlength()>2) {
    if (b.adj!=null) mout.add(new Mueble(10, -(b.id[2]+1), b.pmid().copy(), node.zones.get(0).pt[1]));
    else  mout.add(new Mueble(0, -(b.id[2]+1), b.pmid().copy(), node.zones.get(0).pt[1]));
  }

  if (txequal(roomnonumber(node.code), "Ba")) {
    boolean entsi = false;
    for (Zone z : node.zones) for (Border b : z.borders) if (b.id[4]==5)  if (b.id[2]==0||b.id[2]==1) entsi = true; 
    float [][]ap = {{.25f, .75f}, {.75f, .25f}};
    for (Zone z : node.zones) for (Border b : z.borders) if (b.id[4]==8) {
      PVector lavpos = new PVector(b.pt[0].x*ap[PApplet.parseInt(entsi)][0]+b.pt[1].x*ap[PApplet.parseInt(!entsi)][0], b.pt[0].y*ap[PApplet.parseInt(entsi)][0]+b.pt[1].y*ap[PApplet.parseInt(!entsi)][0]);
      PVector wcpos = new PVector(b.pt[0].x*.5f+b.pt[1].x*.5f, b.pt[0].y*.5f+b.pt[1].y*.5f);
      PVector duchpos = new PVector(b.pt[0].x*ap[PApplet.parseInt(entsi)][1]+b.pt[1].x*ap[PApplet.parseInt(!entsi)][1], b.pt[0].y*ap[PApplet.parseInt(entsi)][1]+b.pt[1].y*ap[PApplet.parseInt(!entsi)][1]);
      mout.add(new Mueble(1, -(b.id[2]+1), lavpos, node.zones.get(0).pt[1]));
      mout.add(new Mueble(3, -(b.id[2]+1), wcpos, node.zones.get(0).pt[1]));
      mout.add(new Mueble(5, -(b.id[2]+1), duchpos, node.zones.get(0).pt[1]));
    }
  }
  if (txequal(roomnonumber(node.code), "Li")) {
    PVector mesapos = new PVector (0, 0);
    PVector livinpos = new PVector (0, 0);
    PVector mesa2pos = new PVector (0, 0);
    Zone zodraw = node.zones.get(0);
    if (zodraw.lengthisx()) {
      mesapos = new PVector (wavgfloat(zodraw.pt[0].x, zodraw.pt[1].x, .25f), zodraw.pmid().y);
      livinpos = new PVector (wavgfloat(zodraw.pt[0].x, zodraw.pt[1].x, .75f), zodraw.pmid().y-zodraw.ydim()*.15f);
      mesa2pos = new PVector (wavgfloat(zodraw.pt[0].x, zodraw.pt[1].x, .75f), zodraw.pmid().y+zodraw.ydim()*.15f);
    } else {
      mesapos = new PVector (zodraw.pmid().x, wavgfloat(zodraw.pt[0].y, zodraw.pt[1].y, .25f));
      livinpos = new PVector (zodraw.pmid().x-zodraw.xdim()*.15f, wavgfloat(zodraw.pt[0].y, zodraw.pt[1].y, .75f));
      mesa2pos = new PVector (zodraw.pmid().x+zodraw.xdim()*.15f, wavgfloat(zodraw.pt[0].y, zodraw.pt[1].y, .75f));
    }                
    mout.add(new Mueble(6, 0, mesapos, node.zones.get(0).pt[1]));
    mout.add(new Mueble(2, PApplet.parseInt(!zodraw.lengthisx())*3, livinpos, node.zones.get(0).pt[1]));
    mout.add(new Mueble(4, PApplet.parseInt(!zodraw.lengthisx()), mesa2pos, node.zones.get(0).pt[1]));
  }
  if (txequal(roomnonumber(node.code), "Ki")) {
    PVector livinpos = new PVector (0, 0);
    PVector mesa2pos = new PVector (0, 0);
    Zone zodraw = node.zones.get(0);
    if (zodraw.lengthisx()) {
      livinpos = new PVector (wavgfloat(zodraw.pt[0].x, zodraw.pt[1].x, .25f), zodraw.pmid().y);
      mesa2pos = new PVector (wavgfloat(zodraw.pt[0].x, zodraw.pt[1].x, .75f), zodraw.pmid().y);
    } else {
      livinpos = new PVector (zodraw.pmid().x, wavgfloat(zodraw.pt[0].y, zodraw.pt[1].y, .25f));
       mesa2pos = new PVector (zodraw.pmid().x, wavgfloat(zodraw.pt[0].y, zodraw.pt[1].y, .75f));
    }                
    mout.add(new Mueble(9, PApplet.parseInt(!zodraw.lengthisx()), livinpos, node.zones.get(0).pt[1]));
    mout.add(new Mueble(91, PApplet.parseInt(!zodraw.lengthisx()), mesa2pos, node.zones.get(0).pt[1]));
  }





  return mout;
}

public void drawmueble(int tipo, int rot, PVector pos, PVector pos2) {
  PVector scpos  = scgr(pos);
  PVector scpos2  = scgr(pos2);
  float scgrid= scsif(grunit);
  rectMode(CORNER);
  pushMatrix();
  translate(scpos.x, scpos.y);
  rotate(HALF_PI*rot);
  translate(-scpos.x, -scpos.y);
  strokeWeight(1*scand);
  if (tipo%2==0) stroke(180);
  if (tipo%2==1) stroke(200);
  noFill();
  if (tipo==0) {
    PVector bed = new PVector(1.6f*grscale, 2*grscale);
    PVector sheet = new PVector(1.6f*grscale, .6f*grscale);
    PVector sheet2 = new PVector(1.6f*grscale, .9f*grscale);
    PVector pillow = new PVector(.4f*grscale, .2f*grscale);
    PVector buro = new PVector(.4f*grscale, .4f*grscale);
    rect(scpos.x-bed.x*.5f, scpos.y, bed.x, bed.y);
    rect(scpos.x-bed.x*.5f, scpos.y, sheet.x, sheet.y);
    rect(scpos.x-bed.x*.5f, scpos.y, sheet2.x, sheet2.y);
    rect(scpos.x+(.2f*grscale)-bed.x*.5f, scpos.y+(.2f*grscale), pillow.x, pillow.y);
    rect(scpos.x+(1*grscale)-bed.x*.5f, scpos.y+(.2f*grscale), pillow.x, pillow.y);
    rect(scpos.x-(.6f*grscale)-bed.x*.5f, scpos.y+(.2f*grscale), buro.x, buro.y);
    rect(scpos.x+bed.x+(.2f*grscale)-bed.x*.5f, scpos.y+(.2f*grscale), buro.x, buro.y);
  }

  if (tipo==1) {
    PVector lavabo = new PVector (.8f*grscale, .4f*grscale);
    PVector circ  = new PVector (.4f*grscale, .3f*grscale);
    rect(scpos.x-lavabo.x*.5f, scpos.y, lavabo.x, lavabo.y);
    ellipse(scpos.x, scpos.y+lavabo.y*.5f, circ.x, circ.y);
    line(scpos.x, scpos.y, scpos.x, scpos.y+lavabo.y*.5f);
  }

  if (tipo==2) {  //SOFA 3  CAMBIAR A 2
    PVector mueble =  new PVector(2.2f*grscale, .8f*grscale);
    PVector cojin =  new PVector(.6f*grscale, .6f*grscale);
    rect(scpos.x-mueble.x*.5f, scpos.y-mueble.y*.5f, mueble.x, mueble.y, mueble.y*.2f);
    rect(scpos.x+grscale*.2f-mueble.x*.5f, scpos.y+mueble.y-mueble.y*.5f, cojin.x, -cojin.y, mueble.y*.2f);
    rect(scpos.x+grscale*.8f-mueble.x*.5f, scpos.y+mueble.y-mueble.y*.5f, cojin.x, -cojin.y, mueble.y*.2f);
    rect(scpos.x+grscale*1.4f-mueble.x*.5f, scpos.y+mueble.y-mueble.y*.5f, cojin.x, -cojin.y, mueble.y*.2f);
    line(scpos.x+grscale*.2f-mueble.x*.5f, scpos.y+grscale*.2f-mueble.y*.5f, scpos.x+mueble.x*.5f-grscale*.2f, scpos.y+grscale*.2f-mueble.y*.5f);
  }
  if (tipo==3) {
    PVector tapa =  new PVector(.5f*grscale, .25f*grscale);
    PVector retrete =  new PVector(.3f*grscale, .45f*grscale);
    rect(scpos.x-tapa.x*.5f, scpos.y, tapa.x, tapa.y, tapa.y*.2f);
    rect(scpos.x+grscale*.1f-tapa.x*.5f, scpos.y+tapa.y+grscale*.05f, retrete.x, retrete.y, tapa.y*.2f);
  }
  if (tipo==4) {
    PVector mesa =  new PVector(1.2f*grscale, .4f*grscale);
    rect(scpos.x-mesa.x*.5f, scpos.y-mesa.y*.5f, mesa.x, mesa.y, mesa.y*.2f);
  }
  if (tipo==5) {
    PVector ducha =  new PVector(.8f*grscale, .8f*grscale);
    rect(scpos.x-ducha.x*.5f, scpos.y, ducha.x, ducha.y);
    ellipse(scpos.x, scpos.y+ducha.y*.3f, .1f*grscale, .1f*grscale);
  }
  if (tipo==6) {
    PVector mesa =  new PVector(.7f*grscale, .7f*grscale);
    PVector asientos =  new PVector(1.1f*grscale, 1.1f*grscale);
    ellipse(scpos.x, scpos.y, mesa.x, mesa.y);
    arc(scpos.x, scpos.y, asientos.x, asientos.y, PI, PI+QUARTER_PI);
    arc(scpos.x, scpos.y, asientos.x, asientos.y, -.4f*PI, -.4f*PI+QUARTER_PI);
    arc(scpos.x, scpos.y, asientos.x, asientos.y, .3f*PI, .3f*PI+QUARTER_PI);
  }
  if (tipo==7) {
    for (float x=scpos.x; x<scpos2.x; x+=scgrid) for (float y=scpos.y; y<scpos2.y; y+=scgrid) {
      if (x+scgrid<=scpos2.x&&y+scgrid<=scpos2.y) {
        rect(x, y, scgrid, scgrid);
        line(x, y, x+scgrid, y+scgrid);
        line(x+scgrid, y, x, y+scgrid);
      } else if (x+scgrid>scpos2.x&&y+scgrid<=scpos2.y) {
        rect(x, y, scpos2.x-x, scgrid);
        line(x, y, scpos2.x, y+scgrid);
        line(scpos2.x, y, x, y+scgrid);
      } else {
        rect(x, y, scgrid, scpos2.y-y);
        line(x, y, x+scgrid, scpos2.y);
        line(x+scgrid, y, x, scpos2.y);
      }
    }
  }

  if (tipo==8) {
    PVector bed = new PVector(1.2f*grscale, 2*grscale);
    PVector sheet = new PVector(1.2f*grscale, .6f*grscale);
    PVector sheet2 = new PVector(1.2f*grscale, .9f*grscale);
    PVector pillow = new PVector(.8f*grscale, .2f*grscale);
    PVector buro = new PVector(.4f*grscale, .4f*grscale);
    rect(scpos.x-bed.x*.5f, scpos.y, bed.x, bed.y);
    rect(scpos.x-bed.x*.5f, scpos.y, sheet.x, sheet.y);
    rect(scpos.x-bed.x*.5f, scpos.y, sheet2.x, sheet2.y);
    rect(scpos.x+(.2f*grscale)-bed.x*.5f, scpos.y+(.2f*grscale), pillow.x, pillow.y);
    rect(scpos.x-(.6f*grscale)-bed.x*.5f, scpos.y+(.2f*grscale), buro.x, buro.y);
  }

  if (tipo==9) {
    PVector tarja = new PVector(.6f*grscale, .4f*grscale);
    rect(scpos.x-tarja.x*.5f, scpos.y-tarja.y*.5f, tarja.x, tarja.y);
    line(scpos.x, scpos.y-tarja.y*.5f, scpos.x, scpos.y-tarja.y*.5f+.2f*grscale);
  }

  if (tipo==91) {
    PVector tarja = new PVector(.6f*grscale, .4f*grscale);
    rect(scpos.x-tarja.x*.5f, scpos.y-tarja.y*.5f, tarja.x, tarja.y);
    line(scpos.x, scpos.y-tarja.y*.5f, scpos.x, scpos.y+tarja.y*.5f);
    line(scpos.x-tarja.x*.5f, scpos.y, scpos.x+tarja.x*.5f, scpos.y);
  }


  if (tipo==10) {
    PVector bed = new PVector(1.6f*grscale, 2*grscale);
    PVector sheet = new PVector(1.6f*grscale, .6f*grscale);
    PVector sheet2 = new PVector(1.6f*grscale, .9f*grscale);
    PVector pillow = new PVector(.4f*grscale, .2f*grscale);

    rect(scpos.x-bed.x*.5f, scpos.y, bed.x, bed.y);
    rect(scpos.x-bed.x*.5f, scpos.y, sheet.x, sheet.y);
    rect(scpos.x-bed.x*.5f, scpos.y, sheet2.x, sheet2.y);
    rect(scpos.x+(.2f*grscale)-bed.x*.5f, scpos.y+(.2f*grscale), pillow.x, pillow.y);
    rect(scpos.x+(1*grscale)-bed.x*.5f, scpos.y+(.2f*grscale), pillow.x, pillow.y);
  }




  popMatrix();
}
Drawzones dz;
public void setupdz() {
  dz = new Drawzones();
}
public void drawdz() {
  if (dr.state)  dz.displaypoint();
  if (dr.state)  dz.display();
}
public void pressdz() {

   if (!dr.isover())if (dr.state) dz.press();
  if (dz.zns.size()>0) {
    if (dr.isover())if (dr.state){
      setuphoinedges(dz.zns); 
      recalctree();
      recalchouse();
      dz.zns = new ArrayList <Zone>(); 
    }
  }
}

class Drawzones {
  ArrayList <Zone> zns;
  PVector p00, p11;

  Drawzones() {
    zns = new ArrayList <Zone>();
  }
  public void displaypoint() {
    pushStyle();
    strokeWeight(2*scand);
    stroke(50);
    noFill();

    PVector gridedmouse =scgr(mouseshrink(new PVector (mouseX, mouseY))); 
    if (validarea(zns)) {
      ellipse(gridedmouse.x, gridedmouse.y, 7*scand, 7*scand);

      line(gridedmouse.x-10*scand, gridedmouse.y, gridedmouse.x+10*scand, gridedmouse.y);
      line(gridedmouse.x, gridedmouse.y-10*scand, gridedmouse.x, gridedmouse.y+10*scand);
    }
    strokeWeight(2*scand);
    stroke(150);
    if (p00!=null)  hiddenrec(scgr(p00).x, scgr(p00).y, gridedmouse.x, gridedmouse.y);




    popStyle();
  }
  public void display() {
    if (zns!=null) for (int i=0; i<zns.size(); i++) zns.get(i).displaycreate();
  }
  public void press() {
    if (validarea(zns))if (p00==null&&p11==null) p00  = mouseshrink(new PVector (mouseX, mouseY));
    else if (p00!=null&&p11==null) {
      if (validarea(zns)) {
        p11  = mouseshrink(new PVector (mouseX, mouseY));
        zns.add(new Zone(p00, p11));
        p00=null;
        p11=null;
      }
    }
  }
  public void emptyzones() {
    zns = new ArrayList <Zone>();
    p00=null;
    p11=null;
  }
}
public void hiddenrec(float p00x, float p00y, float p11x, float p11y) {
  daline(new PVector (p00x, p00y), new PVector (p00x, p11y), 10*scand); 
  daline(new PVector (p00x, p11y), new PVector (p11x, p11y), 10*scand); 
  daline(new PVector (p11x, p11y), new PVector (p11x, p00y), 10*scand); 
  daline(new PVector (p11x, p00y), new PVector (p00x, p00y), 10*scand);
}

public void hashrectangle(float ax, float ay, float bx, float by) {
 rectMode(CORNERS);
 //fill(255,0,0);
  if (bx>ax) for (float x=ax; x<bx;x+=grunit) if (by>ay) for (float y=ay; y<by;y+=grunit){
  PVector beg = scgr(new PVector(x,y));
  PVector end = scgr(new PVector(x+grunit,y+grunit));
  PVector midmas =  scgr(new PVector(x+grunit*.5f,y+grunit*.5f)); 
    //rect(beg.x,beg.y,end.x,end.y); 
  daline(beg,end, 5*scand);
  daline(new PVector (midmas.x,beg.y), new PVector (end.x,midmas.y,5*scand),5*scand);
    daline(new PVector (beg.x,midmas.y), new PVector (midmas.x,end.y,5*scand),5*scand);
 }

  //hiddenrec(ax, ay, bx, by);
  //for (float i=0; i<1; i+=.1) daline(new PVector(ax, ay*i+by*(1-i)), new PVector(ax*i+bx*(1-i), ay), 5*scand);
  //for (float i=0; i<1; i+=.1) daline(new PVector(bx, ay*i+by*(1-i)), new PVector (ax*i+bx*(1-i), by), 5*scand);


  //for (float i=0;i<1;i+=.1) line(p00x*i+p11x*(1-i),p00y*(1-i)+p11y*i,p00x*(1-i)+p11x*i,p00y*i+p11y*(1-i));
}

public boolean validarea(ArrayList <Zone> czones) {
  boolean bout = true;
  if (!(grpos.x<mouseX&&mouseX<grpos.x+grsize.x&&grpos.y<mouseY&&mouseY<grpos.y+grsize.y)) bout = false;
  for (int i=0; i<czones.size(); i++) {
    PVector p0 = scgr(czones.get(i).pt[0]);
    PVector p1 = scgr(czones.get(i).pt[1]);
    if (p0.x<mouseX&&p1.x>mouseX&&p0.y<mouseY&&p1.y>mouseY) bout = false;
  }
  return bout;
}


boolean evolve;


public float fitnessat(House ho,float [] att) {
  float f=0;
  House temph  = ho.clonenewgenes(att);
  f-=temph.totfit;
  return f;
}
class Atribute {
  String name;
  float min;
  float max;
  Atribute(String _name, float _min, float _max) {
    name=_name;
    min=_min;
    max=_max;
  }
  public Atribute clone () {
    Atribute clon = new Atribute (name, min, max);
    return clon;
  }
}
class Population {
  Individual [] pop;
  int nPop;
  Atribute [] at;
  Population(House ho, int _nPop,Atribute [] _at) {
    nPop = _nPop;
    at = _at;
    pop = new Individual[nPop];
    for (int i=0; i<nPop; i++) {   
      pop[i] = new Individual(at);
      pop[i].evaluate(ho);
    }
    pop = orderInds(pop);
  }
  public Individual select() {
    int which = (int)floor(((float)nPop-1e-6f)*(1.0f-sq(random(0, 1))));
    if (which == nPop) which = 0;
    return pop[which];
  }
  public Individual sex(Individual a, Individual b) {
    Individual c = new Individual(at);
    for (int i=0; i<c.genes.length; i++) {
      if (random(0, 1)<0.5f) c.genes[i] = a.genes[i];
      else c.genes[i] = b.genes[i];
    }
    c.mutate();
    c.inPhens();
    return c;
  }
  public void evolve(House ho) {
    Individual a = select();
    Individual b = select();
    Individual x = sex(a, b);
    pop[0] = x;
    x.evaluate(ho);
    pop = orderInds(pop);
  }
  class Individual {  
    float iFit;
    int [] genes;
    float [] phenos, phenosmin, phenosmax;
    Atribute[] a;
    Individual(Atribute [] _a) {
      a=_a;
      iFit = 0;
      genes  = new int [a.length];
      phenos = new float [genes.length];
      phenosmin = new float [phenos.length];
      phenosmax = new float [phenos.length];
      for (int i=0; i<phenosmin.length; i++) phenosmin [i] = a[i].min;
      for (int i=0; i<phenosmax.length; i++) phenosmax [i] = a[i].max;    
      for (int i=0; i<genes.length; i++) genes[i] = (int) random(256);
      for (int i=0; i<phenos.length; i++) phenos [i] = map(genes[i], 0, 256, phenosmin[i], phenosmax[i]);
    }
    public void inGens() {
      genes = new int [a.length];
      for (int i=0; i<genes.length; i++) genes[i] = (int) random(256);
    }
    public void inPhens() {
      for (int i=0; i<phenos.length; i++) phenos [i] = map(genes[i], 0, 256, phenosmin[i], phenosmax[i]);
    }
    public void mutate() {
      for (int i=0; i<genes.length; i++) if (random(100)<5)  genes[i] = (int) random(256);
    }
    public void evaluate(House ho) {
      iFit = fitnessat(ho,phenos);
    }
    public Individual clone() {
      Atribute as[] = new Atribute [a.length];
      for (int i=0; i<a.length; i++) as[i] = a[i].clone();
      Individual clon = new Individual(as);
      return clon;
    }
  }  
  public Individual []  orderInds(Individual [] inarr) {
    Individual tmp = inarr[0].clone();
    for (int i = 0; i < inarr.length; i++) for (int j = i + 1; j < inarr.length; j++) if (inarr[i].iFit > inarr[j].iFit) {
      tmp = inarr[i];
      inarr[i] = inarr[j];
      inarr[j] = tmp;
    }
    return inarr;
  }
}
PVector grpos, grsize, usmouse, grmouse;
float grscale, grunit;

public void setupgr() {
  grpos = new PVector(width*.5f+25*scand,40*scand);
  grsize= new PVector((width*.5f)-(50*scand), (height)-(105*scand));
  grscale =40*scand;
  grunit =.8f;
}
public void drawgr() {
  rectMode(CORNER);
  strokeWeight(1);
  stroke(230);
  noFill();
  rect(grpos.x, grpos.y, grsize.x, grsize.y);
  float us = grunit*grscale;
  for (int x=0; x<=PApplet.parseInt(grsize.x/us); x++) for (int y=0; y<=PApplet.parseInt(grsize.y/us); y++) {
    noFill();
    if (x<PApplet.parseInt(grsize.x/us)&&y<PApplet.parseInt(grsize.y/us)) rect((x*us)+grpos.x, (y*us)+grpos.y, us, us);
    if ((x==PApplet.parseInt(grsize.x/us))&&(y<PApplet.parseInt(grsize.y/us))) rect((x*us)+grpos.x, (y*us)+grpos.y, grsize.x-(x*us), us);
    if ((y==PApplet.parseInt(grsize.y/us))&&(x<PApplet.parseInt(grsize.x/us))) rect((x*us)+grpos.x, (y*us)+grpos.y, us, grsize.y-(y*us));
  }
}
public PVector scgr(PVector pos) {
  return  new PVector ((pos.x*grscale)+grpos.x, (pos.y*grscale)+grpos.y);
}
public PVector inversescgr(PVector pos){
 return new PVector ((pos.x-grpos.x)/grscale, (pos.y-grpos.y)/grscale);
}
public PVector mouseshrink(PVector pos){
      return new PVector (rg(inversescgr(pos).x), rg(inversescgr(pos).y));
}
public float scgrfx(float pos) {
  return (pos*grscale)+grpos.x;
}
public float scgrfy(float pos) {
  return (pos*grscale)+grpos.y;
}
public float scsif(float pos) {
 return   pos*grscale;
}
public float rg(float fin) {
  float fout = fin + grunit*.5f;
  fout = roundit(fout-(fout%grunit), 2);
  return fout;
}
public void grscaleDEhouse(House hoin){
  float grscaleX = grsize.x/housesizex0y1(ho,0);
  float grscaleY = grsize.y/housesizex0y1(ho,1);
  if (grscaleX<grscaleY) grscale = grscaleX;
  else grscale = grscaleY;
  //println("x:"+grsize.x/grscale+ " y:" +grsize.y/grscale);
  //println("x:"+housesizex0y1(ho,0) + " y:" + housesizex0y1(ho,1));
  
}
public float housesizex0y1(House hoin, int x0y1){
  float vout = 0 ;
  if (x0y1==0) vout = hoin.homedges.get(0).pt[1].x;
  if (x0y1==1) vout = hoin.homedges.get(0).pt[1].y;
  if (x0y1==0) for (int i=0; i<hoin.homedges.size();i++) if (hoin.homedges.get(i).pt[1].x>vout) vout = hoin.homedges.get(i).pt[1].x;
   if (x0y1==1) for (int i=0; i<hoin.homedges.size();i++) if (hoin.homedges.get(i).pt[1].y>vout) vout = hoin.homedges.get(i).pt[1].y;   
  return vout;
}
//                                                                              CLONES
public int [] cloneintarr (int inarr[]) {
  int outarr []= new int [inarr.length];
  for (int i=0; i<outarr.length; i++) outarr[i] = inarr[i];
  return outarr;
}
public PVector [] clonevecarr (PVector inarr[]) {
  PVector outarr []= new PVector [inarr.length];
  for (int i=0; i<outarr.length; i++) outarr[i] = inarr[i].copy();
  return outarr;
}
public String [] clonestarr(String [] stin) {
  String [] stout = new String [stin.length];
  for (int i=0; i<stin.length; i++) stout[i] = stin[i];
  return stout;
}
public ArrayList <Border> cloneborderslist(ArrayList <Border> bin) {
  ArrayList <Border> bout = new ArrayList <Border> ();
  if (bin!=null)for (int i=0; i<bin.size(); i++) bout.add(bin.get(i).cloneit());
  return bout;
}

public ArrayList <Mueble> clonemuebles(ArrayList <Mueble> bin) {
  ArrayList <Mueble> bout = new ArrayList <Mueble> ();
  if (bin!=null)for (int i=0; i<bin.size(); i++) bout.add(bin.get(i).cloneit());
  return bout;
}







public int factorial(int num) {
  if (num>12) println("numero factorial muy grande");
  return fact(num);
}
public int fact(int num) {
  if (num <= 1) return 1;
  else return num*fact(num - 1);
}                                   
public int min0(int num) {
  if (num>=0) return num;
  else return 0;
}


public float roundit(float numin, int dec) {
  float dec10 = pow(10, dec);
  float roundout = round(numin * dec10)/dec10;
  return roundout;
}
public float roundspan(float fin, float spanu) {
  float fout = fin + spanu*.5f;
  fout = roundit(fout-(fout%spanu), 2);
  return fout;
}
public float avgfloat(float a, float b) {
  return a*.5f+b*.5f;
}
public float wavgfloat(float a, float b, float wa) {
  return a*wa+b*(1-wa);
}
public float divNaN0(float a, float b) {
  if (a==0&&b==0) return 0;
  else return a/b;
} 
public float [] ranflarr(int num, float min, float max) {
  float rout[] = new float [num];
  for (int i=0; i<rout.length; i++) rout [i] = random(min, max);
  return rout;
}
public float min0max1fl(int min0max1, float a, float b) {
  float fout;
  if ((min0max1==0&&a<b)||(min0max1==1&&a>b))  fout=a;
  else fout =b;
  return fout;
}
//VECTOR
public PVector avgvector(PVector a, PVector b) {
  return new PVector (a.x*.5f+b.x*.5f, a.y*.5f+b.y*.5f);
}
public PVector wavgvector(PVector a, PVector b, float wa) {
  return new PVector (a.x*wa+b.x*(1-wa), a.y*wa+b.y*(1-wa));
}

public boolean txequal(String a, String b) {
  if (a==null||b==null) return false;
  else {
    int al= a.length();
    int bl= b.length();
    int minl;
    boolean bout = true;
    if (al!=bl) bout = false;
    if (al<bl) minl = al;
    else minl = bl;
    for (int i=0; i<minl; i++) if (a.charAt(i)!=b.charAt(i)) bout = false; 
    return bout;
  }
}

public ArrayList <String> removeemptyst(ArrayList <String> stin) {
  ArrayList <String> stout = new ArrayList <String> ();
  for (int i=0; i<stin.size(); i++) if (!txequal(stin.get(i), "")) stout.add(stin.get(i));
  return stout;
}
public String [] removeemptystarr(String [] stin) {
  ArrayList <String> stout = new ArrayList <String> ();
  for (int i=0; i<stin.length; i++) if (!txequal(stin[i], "")) stout.add(stin[i]);
  String starrout[] = new String [stout.size()];
  for (int i=0; i<starrout.length; i++) starrout[i] = stout.get(i);
  return starrout;
} 
public String [] subtostarr(String [] stin, String strremove) {
  ArrayList <String> stout = new ArrayList <String> ();
  for (int i=0; i<stin.length; i++) if (!txequal(stin[i], strremove)) stout.add(stin[i]);
  String starrout[] = new String [stout.size()];
  for (int i=0; i<starrout.length; i++) starrout[i] = stout.get(i);
  starrout = getroomNAMES(starrout);
  return starrout;
}
public String [] addtostarr(String [] inarr, String newstr) {//addtostarr
  String stout [] = new String [inarr.length+1];
  for (int i=0; i< inarr.length; i++) stout[i] = inarr[i];
  stout[inarr.length] = newstr;
  stout = getroomNAMES(stout);
  return stout;
}
//                                                                                        permutations
public String[] permutation01(String [] pre, float num) {
  int numin = factorial(pre.length)-1;
  if (num<1)  numin =PApplet.parseInt(map(num, 0, 1, 0, factorial(pre.length))); 
  String newA[] = perm(pre, 0, new ArrayList <String[]> (), numin);
  return newA;
}
public String[]  perm(String[] iA, int s, ArrayList <String[]> igm, int nume) {    
  for (int i = s; i < iA.length; i++) {
    String temp = iA[s];
    iA[s] = iA[i];
    iA[i] = temp;
    perm(iA, s + 1, igm, nume);
    iA[i] = iA[s];
    iA[s] = temp;
  }
  if (s == iA.length - 1) {
    String toadd= "";
    for (int i=0; i<iA.length-1; i++) toadd = toadd + iA[i] + ",";
    toadd = toadd + iA[iA.length-1];   
    igm.add(split(toadd, ","));
  }
  String [] ig1 = null;
  if (igm.size()>nume)  ig1 = igm.get(nume);
  return ig1;
}
public boolean vectorsequal(PVector a, PVector b) {
  return a.x==b.x&&a.y==b.y;
}


public void daline(PVector a, PVector b, float space) {
  float distper = (space/PVector.dist(a, b))/2;
  boolean odd = true;
  for (float i=0; i<1; i+=distper) {
    float nexti = i+distper;
    PVector p1 = new PVector(b.x*i+a.x*(1-i), b.y*i+a.y*(1-i));
    PVector p2 = new PVector(b.x*nexti+a.x*(1-nexti), b.y*nexti+a.y*(1-nexti));
    if (PVector.dist(p2, p1)<PVector.dist(b, p1)) if (odd) line(p1.x, p1.y, p2.x, p2.y);
    if (PVector.dist(p2, p1)>=PVector.dist(b, p1))if (odd) line(p1.x, p1.y, b.x, b.y);
    odd = !odd;
  }
}


public ArrayList <Zone> clonezonelist(ArrayList <Zone> zlistin) {
  ArrayList <Zone> zlistout = new ArrayList <Zone> ();
  for (int i=0; i<zlistin.size(); i++) zlistout.add(zlistin.get(i).cloneit());
  return zlistout;
}
public Nodegene [] clonengarrwithgenes(Nodegene [] ngin, float [] genes) {
  Nodegene []  nout = new Nodegene [ngin.length];
  for (int i=0; i<ngin.length; i++) nout[i] = ngin[i].cloneactgene(genes[i]);
  return nout;
}









public boolean ifischilda(String loc) {                                      
  boolean bout = false;
  if (loc.charAt(loc.length()-1)=='0'||PApplet.parseInt(loc.charAt(loc.length()-1))==PApplet.parseInt("0 ")) bout = true;
  return bout;
}
public boolean ischildx(float g0) {
  boolean bout; 
  if (g0<.5f) bout=true;
  else bout = false;
  return bout;
}
public boolean zonewithinline(Zone fzone, float pline0, float pline1, boolean child1, boolean childx) {
  if       (child1&&childx &&fzone.pt[0].x<pline0) return true;
  else if  (child1&&!childx&&fzone.pt[0].y<pline0) return true;
  else if  (!child1&&childx&&fzone.pt[1].x>pline1) return true;
  else if (!child1&&!childx&&fzone.pt[1].y>pline1) return true;
  else return false;
}

public float pminmax(int min0max1, ArrayList <Zone> fzones, boolean childx) {
  float fout;
  if (fzones.size()==0) {
    fout = 0;
  } else if (min0max1==0) {
    if (childx) {
      fout = fzones.get(0).pt[0].x;
      for (Zone f : fzones) if (f.pt[0].x<fout) fout = f.pt[0].x;
    } else {
      fout = fzones.get(0).pt[0].y;
      for (Zone f : fzones) if (f.pt[0].y<fout) fout = f.pt[0].y;
    }
  } else {
    if (childx) {
      fout = fzones.get(0).pt[1].x;
      for (Zone f : fzones) if (f.pt[1].x>fout) fout = f.pt[1].x;
    } else {
      fout = fzones.get(0).pt[1].y;
      for (Zone f : fzones) if (f.pt[1].y>fout) fout = f.pt[1].y;
    }
  }
  return fout;
}

public PVector locpos(String loc, PVector treepos, PVector treesize) {
  PVector pv;
  float pvx = treepos.x;
  for (int i=1; i<loc.length(); i++) {
    if (loc.charAt(i)=='0'||PApplet.parseInt(loc.charAt(i))==PApplet.parseInt("0 ")) {
      pvx = pvx - treesize.x/pow(2, i);
    }
  }
  for (int i=1; i<loc.length(); i++) {
    if (loc.charAt(i)=='1'||PApplet.parseInt(loc.charAt(i))==PApplet.parseInt("1 ")) {
      pvx = pvx + treesize.x/pow(2, i);
    }
  }
  float pvy = (((loc.length())-1) * treesize.y) + treepos.y; 
  pv = new PVector (pvx, pvy);
  return pv;
}
public int getngnum(String txtoget, int nrooms) {
  //min0(nrooms-2)
  int  iout = 0;
  if (txequal(txtoget, "lmin"))iout = 0;
  else if (txequal(txtoget, "lmax")||txequal(txtoget, "pmin"))iout = min0(nrooms-2);
  else if (txequal(txtoget, "pmax")||txequal(txtoget, "smin"))iout = min0(nrooms-2)+1;
  else if (txequal(txtoget, "smax"))iout = min0(nrooms-2)+1+min0(nrooms-1); 
  return iout;
}
public Zone breakfzones(Zone fzone, float pline0, float pline1, boolean child1, boolean childx) {                                                     //ZONE
  if       (child1&&childx ) return new Zone (fzone.pt[0].copy(), new PVector (min0max1fl(0, pline0, fzone.pt[1].x), fzone.pt[1].y));
  else if  (child1&&!childx) return new Zone (fzone.pt[0].copy(), new PVector (fzone.pt[1].x, min0max1fl(0, pline0, fzone.pt[1].y)));
  else if  (!child1&&childx) return new Zone (new PVector(min0max1fl(1, pline1, fzone.pt[0].x), fzone.pt[0].y), fzone.pt[1].copy());
  else if (!child1&&!childx) return new Zone (new PVector(fzone.pt[0].x, min0max1fl(1, pline1, fzone.pt[0].y)), fzone.pt[1].copy()); //fzone.type
  else return null;
}
public ArrayList <Zone> sortzonesbyarea(ArrayList <Zone> inarr) {
  Zone tmp = inarr.get(0).cloneit();
  for (int i = 0; i < inarr.size(); i++) for (int j = i + 1; j < inarr.size(); j++) if (inarr.get(i).area() < inarr.get(j).area()) {
    tmp = inarr.get(i);
    inarr.set(i, inarr.get(j));
    inarr.set(j, tmp);
  }
  return inarr;
}






public Node calcrelative(Node n, ArrayList <Node> other, String relat) {
  Node relative = null;
  if (txequal(relat, "father")) for (Node o : other) {
    if (txequal(n.loc.substring(0, n.loc.length()-1), o.loc)) relative = o;
  } else if (txequal(relat, "childa")) for (Node o : other) {
    if (txequal(n.loc + "0", o.loc)) relative = o;
  } else if (txequal(relat, "childb")) for (Node o : other) {
    if (txequal(n.loc + "1", o.loc)) relative = o;
  } else if (txequal(relat, "brother"))for (Node o : other) if (o.nodei!=n.nodei) {
    if (txequal(n.loc.substring(0, n.loc.length()-1), o.loc.substring(0, o.loc.length()-1)))  relative = o;
  }
  return relative;
}

//ArrayList <Node> calcnodeadj(int noi, Node no, ArrayList <Node> ono) {                                                                                           //calcadj
//  ArrayList <Node> nout = new ArrayList <Node> ();
//  for (int i=0; i<no.zones.size(); i++) for (int j=0; j<ono.size(); j++) if (j!=noi) if (ono.get(j).isleaf) for (int l=0; l<ono.get(j).zones.size(); l++) {
//    if (isadj(no.zones.get(i), ono.get(j).zones.get(l))) {
//      boolean isalreadyinlist = false;
//      for (int m=0; m<nout.size(); m++) if (txequal(nout.get(m).loc, ono.get(j).loc)) isalreadyinlist = true;
//      if (!isalreadyinlist) nout.add(ono.get(j));
//    }
//  }
//  return nout;
//}
public int calcbordertype(ArrayList <Node> allnodes, Node no, Border bo) {
  int iout = 0; 
  if (bo.adj!=null) if (txequal(no.ideals[9], allnodes.get(bo.adj.id[0]).code)) iout = 1;
  if (bo.adj==null) iout = 2;
  if (bo.adj!=null) if (bo.id[0]==bo.adj.id[0]) iout = 3;
  return iout;
}

public void calcbordertyperoom(ArrayList <Node> allnodes, Node no) {
  if (PApplet.parseInt(no.ideals[10])==0) {
    boolean hasdoor = false;
    for (Zone z : no.zones) for (Border b : z.borders) if (b.id[4]==1)if (!hasdoor) {
      no.zones.get(z.id[1]).borders.get(b.id[3]).id[4] = 5;
      hasdoor=true;
    }
    boolean haswindow = false;
    for (Zone z : no.zones) for (Border b : z.borders) if (b.id[4]==2) if (bigborder(b, z)) if (!haswindow) {
      no.zones.get(z.id[1]).borders.get(b.id[3]).id[4] = 6;
      //haswindow=true;
    }
    for (Zone z : no.zones) for (Border b : z.borders) if (b.id[4]==2) if (!haswindow) {
      no.zones.get(z.id[1]).borders.get(b.id[3]).id[4] = 6;
      //haswindow=true;
    }
    for (Zone z : no.zones) for (Border b : z.borders) if (b.id[4]!=5&&b.id[4]!=6&&b.id[4]!=8&&b.id[4]!=3) no.zones.get(z.id[1]).borders.get(b.id[3]).id[4] = 7; 
  }             
  if (PApplet.parseInt(no.ideals[10])==1) {
    boolean hasdoor = false;
    for (Zone z : no.zones) for (Border b : z.borders) if (b.id[4]==1)if (!hasdoor) {
      no.zones.get(z.id[1]).borders.get(b.id[3]).id[4] = 5;
      hasdoor=true;
    }
    boolean hasfurniture = false;
    for (Zone z : no.zones) for (Border b : z.borders)if (b.id[4]!=5) if (bigborder(b, z)) if (!hasfurniture) {
      no.zones.get(z.id[1]).borders.get(b.id[3]).id[4] = 8;
      hasfurniture=true;
    }
    boolean haswindow = false;
    for (Zone z : no.zones) for (Border b : z.borders) if (b.id[4]==2) if (bigborder(b, z)) if (!haswindow) {
      no.zones.get(z.id[1]).borders.get(b.id[3]).id[4] = 6;
      haswindow=true;
    }
    for (Zone z : no.zones) for (Border b : z.borders) if (b.id[4]==2) if (!haswindow) {
      no.zones.get(z.id[1]).borders.get(b.id[3]).id[4] = 6;
      haswindow=true;
    }
    for (Zone z : no.zones) for (Border b : z.borders) if (b.id[4]!=5&&b.id[4]!=6&&b.id[4]!=8&&b.id[4]!=3) no.zones.get(z.id[1]).borders.get(b.id[3]).id[4] = 7; 
  }
  if (PApplet.parseInt(no.ideals[10])==2) {           
    boolean hasentry = false;
    for (Zone z : no.zones) for (Border b : z.borders) if (b.id[4]==1) if (bigborder(b, z)) if (!hasentry) {
      no.zones.get(z.id[1]).borders.get(b.id[3]).id[4] = 4;
      hasentry=true;
    }
    for (Zone z : no.zones) for (Border b : z.borders) if (b.id[4]==0) if (bigborder(b, z)) if (!hasentry) {
      no.zones.get(z.id[1]).borders.get(b.id[3]).id[4] = 4;
      hasentry=true;
    }
    for (Zone z : no.zones) for (Border b : z.borders) if (b.id[4]==0) if (!hasentry) {
      no.zones.get(z.id[1]).borders.get(b.id[3]).id[4] = 4;
      hasentry=true;
    }
    for (Zone z : no.zones) for (Border b : z.borders) if (b.id[4]!=4&&b.id[4]!=3) no.zones.get(z.id[1]).borders.get(b.id[3]).id[4] = 7;
  }
}

public boolean bigborder(Border bo, Zone zo) {
  boolean bout = true;
  for (int b=0; b<zo.borders.size(); b++) if (zo.borders.get(b).bsize()>bo.bsize()) bout = false; 
  return bout;
}
public boolean bigbordertipe(Border bo, Zone zo, int type) {
  boolean bout = true;
  for (int b=0; b<zo.borders.size(); b++) if (zo.borders.get(b).id[4] == type)if (zo.borders.get(b).bsize()>bo.bsize()) bout = false; 
  return bout;
}
public String adjlist (ArrayList <Node> onodes, Node no) {
  String stout = "";
  ArrayList <String> starr = new ArrayList <String> ();
  for (int z=0; z<no.zones.size(); z++) for (int b=0; b<no.zones.get(z).borders.size(); b++) if (no.zones.get(z).borders.get(b).adj!=null) if (!(no.zones.get(z).borders.get(b).adj.id[0]==no.nodei)) {
    boolean alreadyinlist = false;
    String posadj = onodes.get(no.zones.get(z).borders.get(b).adj.id[0]).code;
    for (int s=0; s<starr.size(); s++) if (txequal(posadj, starr.get(s))) alreadyinlist = true;
    if (!alreadyinlist) starr.add(posadj);
  }
  for (int s=0; s<starr.size(); s++) stout += starr.get(s) + " ";
  if (stout.length()>0) stout =  stout.substring(0, stout.length()-1);
  return stout;
}
public ArrayList <Node> addfacadeadj(Node no, ArrayList <Node> alladj, ArrayList <Node> faca) {                                                                         //facadeadj
  //for (int i=0; i<faca.size(); i++) alladj.add(faca.get(i)); 
  for (int i=0; i<no.zones.size(); i++)  for (int j=0; j<faca.size(); j++)  for (int l=0; l<faca.get(j).zones.size(); l++) {
    if (isadj(no.zones.get(i), faca.get(j).zones.get(l))) {
      boolean isalreadyinlist = false;
      for (int m=0; m<alladj.size(); m++) if (txequal(alladj.get(m).loc, faca.get(j).loc)) isalreadyinlist = true;
      if (!isalreadyinlist) alladj.add(faca.get(j));
    }
  }
  return alladj;
}
public boolean isadj(Zone a, Zone b) {                                                                                                                              //adjs
  boolean bout = false;
  if (a.pt[0].x==b.pt[1].x&&within(a.pt[0], a.pt[1], b.pt[0], b.pt[1], 1)) bout = true;
  if (a.pt[1].x==b.pt[0].x&&within(a.pt[0], a.pt[1], b.pt[0], b.pt[1], 1)) bout = true;
  if (a.pt[0].y==b.pt[1].y&&within(a.pt[0], a.pt[1], b.pt[0], b.pt[1], 0)) bout = true;
  if (a.pt[1].y==b.pt[0].y&&within(a.pt[0], a.pt[1], b.pt[0], b.pt[1], 0)) bout = true;
  return bout;
}
public boolean within(PVector a00, PVector a11, PVector b00, PVector b11, int x0y1) {                                                                             //within
  boolean bout = false;
  float a0, a1, b0, b1;
  if (x0y1==0) {
    a0=a00.x;
    a1=a11.x;
    b0=b00.x;
    b1=b11.x;
  } else {
    a0=a00.y;
    a1=a11.y;
    b0=b00.y;
    b1=b11.y;
  }
  if (a0<b0&&a1>b0)bout = true;
  if (a0<b1&&a1>b1)bout = true;
  if (b0<a0&&b1>a0)bout = true;
  if (b0<a1&&b1>a1)bout = true;
  if (a0==b0) bout = true;
  if (a1==b1) bout = true;
  return bout;
}
public boolean sameXY(PVector a, PVector b, boolean isx) {
  boolean bout = false;
  if  (isx) bout =  a.x==b.x;
  if (!isx) bout =  a.y==b.y;
  return bout;
}
public boolean withinXY(PVector a0, PVector a1, PVector b, boolean isx) {
  boolean bout = false;
  if (isx)  bout = b.x>a0.x && b.x<a1.x;
  if (!isx) bout = b.y>a0.y && b.y<a1.y;  
  return bout;
}

public void scaleno(ArrayList <Node> no, float sca) {
  for (int n=0; n<no.size(); n++) for (int z=0; z<no.get(n).zones.size(); z++) {
    PVector newp00 = no.get(n).zones.get(z).pt[0].copy().mult(sqrt(sca));
    PVector newp11 = no.get(n).zones.get(z).pt[1].copy().mult(sqrt(sca)); 
    no.get(n).zones.set(z, no.get(n).zones.get(z).clonenewpts(newp00, newp11));
  }
}
public void gridno(ArrayList <Node> no) {
  for (int n=0; n<no.size(); n++) for (int z=0; z<no.get(n).zones.size(); z++) {  
    PVector newp00 = new PVector (rg(no.get(n).zones.get(z).pt[0].x), rg(no.get(n).zones.get(z).pt[0].y));
    PVector newp11 = new PVector (rg(no.get(n).zones.get(z).pt[1].x), rg(no.get(n).zones.get(z).pt[1].y));
    no.get(n).zones.set(z, no.get(n).zones.get(z).clonenewpts(newp00, newp11));
  }
}
public float calcstrechfactor(ArrayList <Node> no) {
  float scfactor = 1;
  float totidealarea = 0;
  float tothousearea = 0;
  for (int n=0; n<no.size(); n++) if (no.get(n).isleaf) totidealarea +=PApplet.parseFloat(no.get(n).ideals[2]);
  for (int n=0; n<no.size(); n++) if (no.get(n).isleaf) for (int z=0; z<no.get(n).zones.size(); z++) tothousearea += no.get(n).zones.get(z).area();
  if (totidealarea != 0&&tothousearea != 0) scfactor = totidealarea / tothousearea;
  return scfactor;
}
public void removeemptyzones(ArrayList <Node> no) {
  for (int n=0; n<no.size(); n++) if (no.get(n).isleaf) for (int z=0; z<no.get(n).zones.size(); z++) {                               //delete 00 zones
    PVector p00 = no.get(n).zones.get(z).pt[0];
    PVector p11 = no.get(n).zones.get(z).pt[1];
    if (p00.x==p11.x||p00.y==p11.y) no.get(n).zones.remove(z);
  }
  for (int n=0; n<no.size(); n++) if (no.get(n).isleaf) for (int z=0; z<no.get(n).zones.size(); z++) no.get(n).zones.get(z).id[1]=z;
}
public void colorDEcode (String code, float transp, String [][] roomDATA) {                                                       
  float colprim = 255/2;
  if (code!=null) for (int i=0; i<roomDATA.length; i++) if (txequal(code, roomDATA[i][0])) colprim = map (i, 0, roomDATA.length, 0, 255);
  noStroke();
  colorMode(HSB);
  fill(colprim, 255, 255, transp);
  colorMode(RGB);
}

//void drawperimeterlines(ArrayList <Zone> zones) {
//  for (Zone z1 : zones) {
//    FloatList linevals = new FloatList (z1.pt[0].x, z1.pt[1].x, z1.pt[0].y, z1.pt[1].y);
//    FloatList pointsvals [] = new FloatList [4];
//    pointsvals [0] = new FloatList  (z1.pt[0].y, z1.pt[1].y);
//    pointsvals [1] = new FloatList  (z1.pt[0].y, z1.pt[1].y);
//    pointsvals [2] = new FloatList  (z1.pt[0].x, z1.pt[1].x);
//    pointsvals [3] = new FloatList  (z1.pt[0].x, z1.pt[1].x);
//    for (Zone z2 : zones) if (z1.id[1]!=z2.id[1]) if ((linevals.get(0)==z2.pt[1].x))  pointsvals[0].append(z2.pt[0].y);  
//    for (Zone z2 : zones) if (z1.id[1]!=z2.id[1]) if ((linevals.get(0)==z2.pt[1].x))  pointsvals[0].append(z2.pt[1].y); 
//    for (Zone z2 : zones) if (z1.id[1]!=z2.id[1]) if ((linevals.get(1)==z2.pt[0].x))  pointsvals[1].append(z2.pt[0].y);  
//    for (Zone z2 : zones) if (z1.id[1]!=z2.id[1]) if ((linevals.get(1)==z2.pt[0].x))  pointsvals[1].append(z2.pt[1].y); 
//    for (Zone z2 : zones) if (z1.id[1]!=z2.id[1]) if ((linevals.get(2)==z2.pt[1].y))  pointsvals[2].append(z2.pt[0].x);  
//    for (Zone z2 : zones) if (z1.id[1]!=z2.id[1]) if ((linevals.get(2)==z2.pt[1].y))  pointsvals[2].append(z2.pt[1].x); 
//    for (Zone z2 : zones) if (z1.id[1]!=z2.id[1]) if ((linevals.get(3)==z2.pt[0].y))  pointsvals[3].append(z2.pt[0].x);  
//    for (Zone z2 : zones) if (z1.id[1]!=z2.id[1]) if ((linevals.get(3)==z2.pt[0].y))  pointsvals[3].append(z2.pt[1].x); 
//    for (int s=0; s<4; s++) pointsvals[s].sort();
//    for (int s=0; s<2; s++) for (int i=0; i<pointsvals[s].size(); i++) if (i%2==0) line(scgrfx(linevals.get(s)), scgrfy(pointsvals[s].get(i)), scgrfx(linevals.get(s)), scgrfy(pointsvals[s].get(i+1)));
//    for (int s=2; s<4; s++) for (int i=0; i<pointsvals[s].size(); i++) if (i%2==0)  line( scgrfx(pointsvals[s].get(i)), scgrfy(linevals.get(s)), scgrfx(pointsvals[s].get(i+1)), scgrfy(linevals.get(s)));
//  }
//}
class Click {
  PVector pos, size, mid, end;
  String name;
  boolean state, displaystate;
  int type = 0;
  int icon = 0;
  float textsize = 12;
  float scand = 1;
  int con = 200;
  int coff = 100;
  Click(PVector _pos, PVector _size, String _name, int _type) {
    pos=_pos;
    size=_size;
    calcpos();
    name = _name;
    type = _type;
  }
  public void calcpos() {
    mid = new PVector(pos.x+size.x/2, pos.y+size.y/2);
    end = new PVector(pos.x+size.x, pos.y+size.y);
  }
  public void scaletoandroid(float _scand) {
    scand = _scand;
    pos  = new PVector (scand*pos.x, scand*pos.y);
    size  = new PVector (scand*size.x, scand*size.y);
    mid  = new PVector (scand*mid.x, scand*mid.y);
    end  = new PVector (scand*end.x, scand*end.y);
    textsize = textsize * scand;
  }
  public void display() {
    pushStyle();
    rectMode(CORNER);
    textAlign(CENTER, CENTER);
    textSize(textsize);
    stroke(coff);
    strokeWeight(1*scand);
    noFill();
    if (frameCount%30==0) displaystate = false;
    if (isover()) strokeWeight(2*scand);
    if (type==10||type==11) {
      if (displaystate)  noStroke();
      if (displaystate)  fill(con);
      ellipse(mid.x, mid.y, size.x, size.y);
      fill(coff);
      if (type==10) textAlign(LEFT, CENTER);
      if (type==10) text(name, end.x+(size.x*.3f), mid.y);
      if (type==11) textAlign(CENTER, CENTER);
      if (type==11) text(name, mid.x, end.y+(textsize*.8f));
    }

    if (type==20||type==21) {
      stroke(coff);
      if (displaystate)  noStroke();
      if (displaystate)  fill(con);
      if (type==20) rect(pos.x, pos.y, size.x, size.y, size.y/2);
      if (type==21) rect(pos.x, pos.y, size.x, size.y, size.y/4);
      textAlign(CENTER, CENTER);
      if (displaystate)  fill(255);
      else fill(coff);
      text(name, mid.x, mid.y);
    }
    if (type==30||type==31) {
      if (displaystate)  noStroke();
      if (displaystate)  fill(con);
      ellipse(mid.x, mid.y, size.x, size.y);
      if (displaystate) fill(255);
      if (!displaystate) fill(coff);
      drawicon(icon, mid, size);
      fill(coff);
      if (displaystate) fill(con);
      if (type==30) textAlign(CENTER, CENTER);
      if (type==30)text(name, mid.x, end.y+size.y*.15f);
      if (type==31) textAlign(LEFT, CENTER);
      if (type==31) if (isover())text(name, end.x+size.x*.2f, mid.y);
    }
    if (type==32) {
      if (displaystate)  noStroke();
      if (displaystate)  fill(con);
      if (!displaystate) fill(coff);
      drawicon(icon, mid, size);
    }
    if (type==40) {
      stroke(coff);
      if (displaystate)  noStroke();
      if (displaystate)  fill(con);
      if (displaystate) rect(pos.x, pos.y, size.x, size.y, size.y/4);
      textAlign(CENTER, CENTER);
      if (displaystate)  fill(255);
      else fill(coff);
      text(name, mid.x, mid.y);
    }
    if (type==41) {
      textAlign(CENTER, CENTER);
      if (displaystate)  fill(con);
      else fill(coff);
      if (isover()) strokeWeight(2*scand);
      else strokeWeight(1*scand);
      text(name, mid.x, mid.y); 
      line(pos.x, end.y, end.x, end.y);
    }
    strokeWeight(1*scand);
    popStyle();
  }

  public boolean isover() {
    return  (mouseX > pos.x && mouseX < end.x  &&mouseY >pos.y && mouseY < end.y);
  }
  public boolean isoverandpressed() {
    return  (isover()&&mousePressed == true);
  }
  public void presson() {
    if (isover()) state = true;
    if (isover()) displaystate = true;
  }
  public void pressoff() {
    if (isover()) state = false;
  }
}
class Toggle {
  PVector pos, size, mid, end, poshide, posshow;
  String name;
  boolean state;
  int type = 0;
  int icon = 0;
  float textsize = 12;
  float scand = 1;
  int con = 200;
  int coff = 100;
  Toggle(PVector _pos, PVector _size, String _name, int _type) {
    pos=_pos;
    size=_size;
    calcpos();
    name = _name;
    type = _type;
    poshide = pos;
    posshow = pos;
  }
  public void calcpos() {
    mid = new PVector(pos.x+size.x/2, pos.y+size.y/2);
    end = new PVector(pos.x+size.x, pos.y+size.y);
  }
  public void calcposhide(PVector _posshow) {
    poshide = pos;
    posshow = _posshow;
  }
  public void scaletoandroid(float _scand) {
    scand = _scand;
    pos  = new PVector (scand*pos.x, scand*pos.y);
    size  = new PVector (scand*size.x, scand*size.y);
    mid  = new PVector (scand*mid.x, scand*mid.y);
    end  = new PVector (scand*end.x, scand*end.y);
    poshide  = new PVector (scand*poshide.x, scand*poshide.y);
    posshow  = new PVector (scand*posshow.x, scand*posshow.y);
    textsize = textsize * scand;
  }
  public void display() {
    pushStyle();
    textSize(textsize);
    rectMode(CORNER);


    if (type==10||type==11||type==12) {
      stroke(coff);
      fill(255);
      rect(pos.x, pos.y, size.x, size.y, size.y);
      strokeWeight(1*scand);
      if (isover()) strokeWeight(2*scand);
      if (!state) {
        stroke(coff);
        fill(255);
        ellipse(pos.x+(size.y*.5f), mid.y, size.y*1.05f, size.y*1.05f);
      } else {
        noStroke();
        fill(con);
        rect(pos.x, pos.y, size.x, size.y, size.y); 
        fill(255);
        stroke(coff);
        ellipse(end.x-(size.y*.5f), pos.y+(size.y*.5f), size.y*1.05f, size.y*1.05f);
      }      
      fill(coff);
      if (type==11)  textAlign(CENTER, CENTER);
      if (type==11)  text(name, mid.x, end.y+textsize*.8f);
      if (type==12)  textAlign (LEFT, CENTER);
      if (type==12)  text(name, end.x+textsize*.8f, mid.y );
    }
    if (type==20||type==21) {
      stroke(coff);
      noFill();
      strokeWeight(1*scand);
      if (isover()) strokeWeight(2*scand);
      if (state) fill(con);
      if (state) noStroke();
      ellipse(mid.x, mid.y, size.x, size.y);
      fill(coff);
      if (state) fill(coff);
      if (state) noStroke();
      drawicon(icon, mid, size);
      textAlign(CENTER, CENTER);
      fill(coff);
      if (type==20) textAlign(CENTER, CENTER);
      if (type==20) text(name, mid.x, end.y+textsize*.8f);
      if (type==21) textAlign(LEFT, CENTER);
      if (type==21) text(name, end.x+textsize*.8f, mid.y);
    }
    if (type==22) {
      stroke(coff);
      noFill();
      strokeWeight(1*scand);
      if (isover()) strokeWeight(2*scand);
      if (state) fill(con);
      if (state) noStroke();
      if (state) ellipse(pos.x+(size.y*.5f), pos.y+(size.y*.5f), size.x*1.05f, size.y*1.05f);
      fill(coff);
      if (state) fill(coff);
      if (state) noStroke();
      PVector a = new PVector (pos.x+(size.x*.5f), pos.y+(size.y*.5f));
      drawicon(icon, a, size);
      textAlign(CENTER, CENTER);
      if (state) fill(con);
      else fill(coff);
      text(name, pos.x+(size.x*.5f), end.y+textsize*.8f);
    }
    if (type==30||type==31) {
      stroke(coff);
      fill(255);
      strokeWeight(1*scand);
      if (isover()) strokeWeight(2*scand);
      if (!state) {
        rect(poshide.x, poshide.y, size.x, size.y);
        if (type==30)line(pos.x+size.x*.5f-7*scand, pos.y+size.y*.5f, pos.x+size.x*.5f, pos.y+size.y*.5f+7*scand);
        if (type==30) line(pos.x+size.x*.5f+7*scand, pos.y+size.y*.5f, pos.x+size.x*.5f, pos.y+size.y*.5f+7*scand);
        if (type==31)       line(pos.x+(size.x*.4f), pos.y+size.y*.5f-(7*scand), pos.x+(size.x*.7f), pos.y+size.y*.5f);
        if (type==31) line(pos.x+(size.x*.4f), pos.y+size.y*.5f+(7*scand), pos.x+(size.x*.7f), pos.y+size.y*.5f);
      }
      if (state) {
        rect(poshide.x, poshide.y, posshow.x-poshide.x+size.x, posshow.y-poshide.y+size.y);
        if (type==30) line(pos.x+size.x*.5f-7*scand, pos.y+size.y*.5f+7*scand, pos.x+size.x*.5f, pos.y+size.y*.5f);
        if (type==30) line(pos.x+size.x*.5f+7*scand, pos.y+size.y*.5f+7*scand, pos.x+size.x*.5f, pos.y+size.y*.5f);
        if (type==31) line(pos.x+(size.x*.7f), pos.y+size.y*.5f-(7*scand), pos.x+(size.x*.4f), pos.y+size.y*.5f);
        if (type==31) line(pos.x+(size.x*.7f), pos.y+size.y*.5f+(7*scand), pos.x+(size.x*.4f), pos.y+size.y*.5f);
      }
    }

    if (type==40) {
      stroke(coff);
      fill(coff);
      strokeWeight(1*scand);
      if (isover()) strokeWeight(2*scand);
      if (state) fill(con);
      if (state) stroke(con);
      line(pos.x, end.y, end.x, end.y);
      textAlign(CENTER, CENTER);
      text(name, pos.x+(size.x*.5f), pos.y+(size.y*.5f));
    }
    if (type==50) {
      noStroke();      
      fill(con);
      if (state) fill(coff);
      PVector a = new PVector (pos.x+(size.x*.5f), pos.y+(size.y*.5f));
      if (state) drawicon(17, a, size);
      else drawicon(16, a, size);
    }
    popStyle();
  }
  public void turnon() {
    state = true;
    if (type==30||type==31) {
      pos  = new PVector (posshow.x, posshow.y);
      calcpos();
    }
  }
  public void turnoff() {
    state = false;
    if (type==30||type==31) {
      pos  = new PVector (poshide.x, poshide.y);
      calcpos();
    }
  }
  public void press() {
    if (type!=30&&type!=31) if (isover()) state = !state;
    if (type==30||type==31) if (isover()) if (!state) turnon();
    if (type==30||type==31) if (isover()) if (state) turnoff();
  }
  public boolean isover() {
    if (mouseX>pos.x&&mouseX<end.x&&mouseY>pos.y&&mouseY<end.y) return true;
    else return false;
  }
}
class Option {
  PVector pos, size, mid, end, ipos[], imid[], iend[];
  String[] names;
  boolean state, displaystate;
  int type = 0;
  int namei = 0;
  int icon = 0;
  float textsize = 12;
  float scand = 1;
  int con = 100;
  int coff = 200;
  Option(PVector _pos, PVector _size, String _names[], int _type) {
    pos = _pos;
    size=_size;
    names = _names;
    type = _type;
    calcpos();
    if (type==11||type==13) namei=-1;
  }
  public void calcpos() {
    mid = new PVector (pos.x + size.x/2, pos.y + size.y/2);
    end = new PVector (pos.x + size.x, pos.y + size.y   );
    calcipos(type);
  }
  public void scaletoandroid(float _scand) {
    scand = _scand;
    pos = new PVector (pos.x*scand, pos.y*scand);
    size = new PVector (size.x*scand, size.y*scand);
    textsize = textsize * scand;
    calcpos();
  }

  public void calcipos(int type) {
    if (type==10||type==12) {
      ipos = new PVector [names.length];
      imid = new PVector [names.length];
      iend = new PVector [names.length];
      for (int i=0; i<ipos.length; i++) if (i==namei) ipos[i] = new PVector (pos.x, pos.y);
      for (int i=0; i<ipos.length; i++) if (i<namei) ipos[i] = new PVector (pos.x, pos.y+(i*size.y)+size.y);
      for (int i=0; i<ipos.length; i++) if (i>namei) ipos[i] = new PVector (pos.x, pos.y+(i*size.y));
      for (int i=0; i<imid.length; i++) if (i==namei) imid[i] = new PVector (pos.x+size.x*.5f, pos.y+size.y*.5f);
      for (int i=0; i<imid.length; i++) if (i<namei) imid[i] = new PVector (pos.x+size.x*.5f, pos.y+(i*size.y)+size.y*1.5f);
      for (int i=0; i<imid.length; i++) if (i>namei) imid[i] = new PVector (pos.x+size.x*.5f, pos.y+(i*size.y)+size.y*0.5f);
      for (int i=0; i<iend.length; i++) if (i==namei) iend[i] = new PVector (pos.x+size.x, pos.y+size.y);
      for (int i=0; i<iend.length; i++) if (i<namei) iend[i] = new PVector (pos.x+size.x, pos.y+(i*size.y)+size.y*2);
      for (int i=0; i<iend.length; i++) if (i>namei) iend[i] = new PVector (pos.x+size.x, pos.y+(i*size.y)+size.y*1);
    }
    if (type==11) {
      ipos = new PVector [names.length];
      imid = new PVector [names.length];
      iend = new PVector [names.length];
      for (int i=0; i<ipos.length; i++) ipos[i] = new PVector (pos.x, pos.y+size.y+(size.y*i));
      for (int i=0; i<imid.length; i++) imid[i] = new PVector (pos.x+(size.x*.5f), pos.y+size.y+(size.y*i)+(size.y*.5f));
      for (int i=0; i<iend.length; i++) iend[i] = new PVector (pos.x+(size.x), pos.y+size.y+(size.y*i)+(size.y));
    }
    if (type==13) {
      ipos = new PVector [names.length];
      imid = new PVector [names.length];
      iend = new PVector [names.length];
      for (int i=0; i<ipos.length; i++) ipos[i] = new PVector (pos.x+size.x+size.x*i, pos.y);
      for (int i=0; i<imid.length; i++) imid[i] = new PVector (pos.x+size.x+size.x*i+size.x*.5f, pos.y+size.y*.5f);
      for (int i=0; i<iend.length; i++) iend[i] = new PVector (pos.x+size.x+size.x*i+size.x, pos.y+size.y);
    }
    if (type==20||type==30) {
      ipos = new PVector [names.length];
      imid = new PVector [names.length];
      iend = new PVector [names.length];
      for (int i=0; i<ipos.length; i++) ipos[i] = new PVector (pos.x+(size.x*i), pos.y);
      for (int i=0; i<imid.length; i++) imid[i] = new PVector (pos.x+(size.x*i)+(size.x*.5f), pos.y+(size.y*.5f));
      for (int i=0; i<iend.length; i++) iend[i] = new PVector (pos.x+(size.x*i)+(size.x), pos.y+(size.y));
    }
  }
  public void display() {
    pushStyle();
    textSize(textsize);
    if (type==10) {
      stroke(con);
      if (isover()) strokeWeight (2*scand);
      else strokeWeight(1*scand);
      fill(255);
      textAlign(RIGHT, CENTER);
      noFill();
      strokeWeight(1);
      if (state) {
        for (int i=0; i<ipos.length; i++) {
          if (isoveri()[i])  strokeWeight (2);
          else strokeWeight(1);
          fill(255);
          rect(ipos[i].x, ipos[i].y, size.x, size.y, size.y/4);
          fill(con);

          if (i!=namei) text(names[i], iend[i].x-25, imid[i].y);
        }
      }   
      line(end.x-15*scand, mid.y, end.x-10*scand, mid.y+5*scand);
      line(end.x-5*scand, mid.y, end.x-10*scand, mid.y+5*scand);
      fill(con);
      text(names[namei], end.x-20*scand, mid.y);
    }
    if (type==11||type==13) {
      stroke(con);
      if (isover()) strokeWeight (2*scand);
      else strokeWeight(1*scand);
      fill(con);
      PVector a = new PVector (pos.x+(size.x*.5f), pos.y+(size.y*.5f));
      PVector isize = new PVector (size.y, size.y);
      fill(con);
      drawicon(icon, a, isize);
      if (state) for (int i=0; i<ipos.length; i++) {
        textAlign(CENTER, CENTER);
        fill(255);
        if (isoveri()[i])  strokeWeight (2);
        else strokeWeight(1);
        rect(ipos[i].x, ipos[i].y, size.x, size.y, size.y/4);
        fill(con);
        text(names[i], imid[i].x, imid[i].y);
      }
    }
    if (type==12) {
      stroke(con);
      if (isover()) strokeWeight (2*scand);
      else strokeWeight(1*scand);
      fill(255);
      textAlign(CENTER, CENTER);
      noFill();
      strokeWeight(1);
      if (state) {
        for (int i=0; i<ipos.length; i++) {
          if (isoveri()[i])  strokeWeight (2);
          else strokeWeight(1);
          fill(255);
          rect(ipos[i].x, ipos[i].y, size.x, size.y, size.y/4);
          fill(con);
          if (i!=namei) text(names[i], imid[i].x, imid[i].y-5*scand);
        }
      }   
      noStroke();
      fill(255);
      rect(pos.x, pos.y, size.x, size.y, size.y/4);
      stroke(con);
      line(mid.x-5*scand, end.y-9*scand, mid.x-0*scand, end.y-4*scand);
      line(mid.x+5*scand, end.y-9*scand, mid.x-0*scand, end.y-4*scand);
      fill(con);
      text(names[namei], mid.x, mid.y-5*scand);
    }

    if (type==20) {
      rectMode(CORNER);
      textAlign(CENTER, CENTER);
      for (int i=0; i<names.length; i++) if (i!=namei) {     
        noFill();
        stroke(coff);
        strokeWeight(1*scand);

        if (isoveri()[i]) strokeWeight(1.5f*scand);
        rect(ipos[i].x, ipos[i].y, size.x, size.y);
        fill(coff);
        text(names[i], imid[i].x, imid[i].y);
      }
      for (int i=0; i<names.length; i++) if (i==namei) {
        stroke(con);
        strokeWeight(1.5f*scand);
        line(ipos[i].x, ipos[i].y, ipos[i].x, iend[i].y);
        line(ipos[i].x, ipos[i].y, iend[i].x, ipos[i].y);
        line(iend[i].x, ipos[i].y, iend[i].x, iend[i].y);
        fill(con);
        text(names[i], imid[i].x, imid[i].y);
      }
    }
    if (type==30) {

      for (int i=0; i<names.length; i++) if (i!=namei) {     
        fill(coff);
        stroke(coff);
        strokeWeight(1*scand);
        if (isoveri()[i]) strokeWeight(1.5f*scand);
        ellipse( imid[i].x, imid[i].y, 7*scand, 7*scand);
      }
      for (int i=0; i<names.length; i++) if (i==namei) {
        fill(con);
        stroke(con);
        strokeWeight(1*scand);
        if (isoveri()[i]) strokeWeight(1.5f*scand);
        ellipse( imid[i].x, imid[i].y, 7*scand, 7*scand);
      }
    }
    popStyle();
  }
  public boolean otherselectarr(Option [] allsl) {
    boolean bout=false;
    for (Option o : allsl) if (o.state) bout = true;
    if (state) bout = false;
    return bout;
  }
  public boolean isover() {
    if (mouseX>pos.x&&mouseX<end.x&&mouseY>pos.y&&mouseY<end.y) return true;
    else return false;
  }
  public boolean [] isoveri() {
    boolean [] bout = new boolean [names.length];
    for (int i=0; i<bout.length; i++) {
      if (mouseX>ipos[i].x&&mouseX<iend[i].x&&mouseY>ipos[i].y&&mouseY<iend[i].y) bout[i] = true;
      else bout[i] = false;
    }
    return bout;
  }
  public void press() {
    if (type==10||type==11||type==12||type==13) {
      if (isover()) state = !state;
      if (state) for (int i=0; i<names.length; i++) if (i!=namei||type==11||type==13) {
        if (isoveri()[i]) {
          namei = i;
          state = !state;
        }
      }
      calcipos(type);
    }
    if (type==20||type==30) {
      for (int i=0; i<names.length; i++)  if (isoveri()[i]) {
        namei = i;
      }
    }
  }
  public void pressoff() {
    if (type==11||type==13) namei=-1;
  }
}
public void drawicon(int icon, PVector pos, PVector size) {
  if (icon==0) { 
    pushMatrix();
    rect(pos.x-size.x*.35f, pos.y, size.x*.7f, size.y*.1f, size.y*.1f);
    translate(pos.x-size.x*.3f, pos.y+size.y*.05f);
    rotate(QUARTER_PI);
    translate(-pos.x+size.x*.3f, -pos.y-size.y*.05f);
    rect(pos.x-size.x*.3f, pos.y, size.x*.4f, size.y*.1f, size.y*.1f);
    translate(pos.x-size.x*.3f, pos.y+size.y*.05f);
    rotate(-2*QUARTER_PI);
    translate(-pos.x+size.x*.3f, -pos.y-size.y*.05f);
    rect(pos.x-size.x*.3f, pos.y, size.x*.4f, size.y*.1f, size.y*.1f);
    popMatrix();
  }
  if (icon==1) {
    rectMode(CENTER);
    ellipse(pos.x, pos.y-size.y*.3f, size.y*.1f, size.y*.1f);
    rect(pos.x, pos.y+size.y*.1f, size.x*.1f, size.y*.5f, size.y*.1f);
    rect(pos.x-size.x*.1f, pos.y-size.y*.1f, size.x*.2f, size.y*.1f, size.y*.1f);
    rectMode(CORNER);
  }
  if (icon==2) {
    rect(pos.x-size.x*.4f, pos.y-size.y*.05f, size.x*.8f, size.y*.1f, size.y*.1f);
    rect(pos.x-size.x*.05f, pos.y-size.y*.4f, size.x*.1f, size.y*.8f, size.y*.1f);
  }
  if (icon==3) {
    pushMatrix();
    rect(pos.x-size.x*.05f, pos.y-size.y*.3f, size.y*.1f, size.y*.4f, size.y*.1f);
    rect(pos.x-size.x*.35f, pos.y+size.y*.2f, size.x*.7f, size.y*.1f, size.y*.1f);
    translate(size.x*.3f, -size.y*.4f);
    translate(pos.x-size.x*.3f, pos.y+size.y*.05f);
    rotate(HALF_PI);
    rotate(QUARTER_PI);
    translate(-pos.x+size.x*.3f, -pos.y-size.y*.05f);
    rect(pos.x-size.x*.3f, pos.y, size.x*.4f, size.y*.1f, size.y*.1f);
    translate(pos.x-size.x*.3f, pos.y+size.y*.05f);
    rotate(-2*QUARTER_PI);
    translate(-pos.x+size.x*.3f, -pos.y-size.y*.05f);
    rect(pos.x-size.x*.35f, pos.y, size.x*.4f, size.y*.1f, size.y*.1f);
    popMatrix();
  }
  if (icon==4) {
    pushMatrix();
    rect(pos.x-size.x*.3f, pos.y-size.y*+.15f, size.y*.1f, size.y*.3f, size.y*.1f);
    rect(pos.x+size.x*.18f, pos.y-size.y*+.15f, size.y*.1f, size.y*.3f, size.y*.1f);
    rect(pos.x-size.x*.35f, pos.y+size.y*.2f, size.x*.7f, size.y*.1f, size.y*.1f);
    translate(size.x*.3f, -size.y*.4f);
    translate(pos.x-size.x*.3f, pos.y+size.y*.05f);
    rotate(HALF_PI);
    rotate(QUARTER_PI);
    translate(-pos.x+size.x*.3f, -pos.y-size.y*.05f);
    rect(pos.x-size.x*.3f, pos.y, size.x*.4f, size.y*.1f, size.y*.1f);
    translate(pos.x-size.x*.3f, pos.y+size.y*.05f);
    rotate(-2*QUARTER_PI);
    translate(-pos.x+size.x*.3f, -pos.y-size.y*.03f);
    rect(pos.x-size.x*.3f, pos.y, size.x*.4f, size.y*.1f, size.y*.1f);
    popMatrix();
  }

  if (icon==5) {
    pushMatrix();
    rect(pos.x-size.x*.05f, pos.y-size.y*.4f, size.y*.1f, size.y*.5f, size.y*.1f);
    rect(pos.x-size.x*.35f, pos.y+size.y*.2f, size.x*.7f, size.y*.1f, size.y*.1f);
    translate(size.x*.3f, -size.y*.4f);
    translate(pos.x-size.x*.3f, pos.y+size.y*.50f);
    rotate(PI*1.5f);
    rotate( QUARTER_PI);
    translate(-pos.x+size.x*.3f, -pos.y-size.y*.05f);
    rect(pos.x-size.x*.3f, pos.y, size.x*.4f, size.y*.1f, size.y*.1f);
    translate(pos.x-size.x*.3f, pos.y+size.y*.05f);
    rotate( -2*QUARTER_PI);
    translate(-pos.x+size.x*.3f, -pos.y-size.y*.05f);
    rect(pos.x-size.x*.3f, pos.y, size.x*.4f, size.y*.1f, size.y*.1f);
    popMatrix();
  }

  if (icon==6) {
    pushMatrix();
    translate(pos.x, pos.y);
    rotate( -QUARTER_PI);
    translate(-pos.x, -pos.y);
    rect(pos.x-size.y*.15f, pos.y-size.y*.1f, size.x*.5f, size.y*.15f, size.y*.1f);
    rotate( QUARTER_PI);
    popMatrix();
    triangle(pos.x-size.x*.2f, pos.y+size.x*.15f, pos.x-size.x*.2f, pos.y+size.x*.2f, pos.x-size.x*.15f, pos.y+size.x*.2f);
  }
  if (icon==7) {
    pushMatrix();
    translate(pos.x, pos.y);
    rotate( -QUARTER_PI);
    translate(-pos.x, -pos.y);
    rect(pos.x-size.x*.4f, pos.y-size.y*.05f, size.x*.8f, size.y*.1f, size.y*.1f);
    rect(pos.x-size.x*.05f, pos.y-size.y*.4f, size.x*.1f, size.y*.8f, size.y*.1f);
    popMatrix();
  }
  if (icon==8) {
    pushMatrix();
    translate(pos.x, pos.y);
    rotate( -QUARTER_PI);
    translate(-pos.x, -pos.y);
    translate(-size.x*.1f, size.y*.1f);
    rect(pos.x-size.y*.15f, pos.y-size.y*.05f, size.x*.6f, size.y*.15f, size.y*.1f);
    rect(pos.x-size.y*.15f, pos.y-size.y*.3f, size.x*.15f, size.y*.4f, size.y*.1f);
    popMatrix();
  }

  if (icon==9) {
    rect(pos.x-size.x*.4f, pos.y-size.y*.05f, size.x*.8f, size.y*.1f, size.y*.1f);
    //rect(pos.x-size.x*.05, pos.y-size.y*.4, size.x*.1, size.y*.8, size.y*.1);
  }
  if (icon==10) {
    ellipse(pos.x-size.x*.3f, pos.y, size.y*.2f, size.y*.2f);
    ellipse(pos.x, pos.y, size.y*.2f, size.y*.2f);
    ellipse(pos.x+size.x*.3f, pos.y, size.y*.2f, size.y*.2f);
  }
  if (icon==11) {
    rect(pos.x-size.x*.35f, pos.y+size.y*.2f, size.x*.7f, size.y*.1f, size.y*.1f);
  }
  if (icon==12) {
    pushMatrix();
    rect(pos.x-size.x*.3f, pos.y-size.y*+.15f, size.y*.1f, size.y*.3f, size.y*.1f);
    rect(pos.x+size.x*.18f, pos.y-size.y*+.15f, size.y*.1f, size.y*.3f, size.y*.1f);

    rect(pos.x-size.x*.1f, pos.y+size.y*.2f, size.x*.3f, size.y*.1f, size.y*.1f);
    rect(pos.x+size.x*.1f-size.x*.1f, pos.y+size.y*.1f, size.x*.1f, size.y*.3f, size.y*.1f);

    translate(size.x*.3f, -size.y*.4f);
    translate(pos.x-size.x*.3f, pos.y+size.y*.05f);
    rotate(HALF_PI);
    rotate(QUARTER_PI);
    translate(-pos.x+size.x*.3f, -pos.y-size.y*.05f);
    rect(pos.x-size.x*.3f, pos.y, size.x*.4f, size.y*.1f, size.y*.1f);
    translate(pos.x-size.x*.3f, pos.y+size.y*.05f);
    rotate(-2*QUARTER_PI);
    translate(-pos.x+size.x*.3f, -pos.y-size.y*.03f);
    rect(pos.x-size.x*.3f, pos.y, size.x*.4f, size.y*.1f, size.y*.1f);
    popMatrix();
  }
  if (icon==13) { //MINUS
    rect(pos.x-size.x*.2f, pos.y-size.y*.025f, size.x*.4f, size.y*.05f, size.y*.05f);
    //rect(pos.x-size.x*.05, pos.y-size.y*.4, size.x*.1, size.y*.8, size.y*.1);
  }

  if (icon==14) { //PLUS
    rect(pos.x-size.x*.2f, pos.y-size.y*.025f, size.x*.4f, size.y*.05f, size.y*.05f);
    rect(pos.x-size.x*.025f, pos.y-size.y*.2f, size.x*.05f, size.y*.4f, size.y*.05f);
  }
  if (icon==15) { //MINUS
    rect(pos.x-size.x*.3f, pos.y-size.y*.2f, size.x*.6f, size.y*.06f, size.y*.03f);
    rect(pos.x-size.x*.3f, pos.y-size.y*.0f, size.x*.6f, size.y*.06f, size.y*.03f);
    rect(pos.x-size.x*.3f, pos.y+size.y*.2f, size.x*.6f, size.y*.06f, size.y*.03f);
    //rect(pos.x-size.x*.05, pos.y-size.y*.4, size.x*.1, size.y*.8, size.y*.1);
  }
  if (icon==16) { //LOCK OPEN
    rect(pos.x-size.x*.25f, pos.y-size.y*.1f, size.x*.5f, size.y*.1f, size.y*.05f);
    rect(pos.x-size.x*.25f, pos.y+size.y*.2f, size.x*.5f, size.y*.1f, size.y*.05f);
    rect(pos.x-size.x*.25f, pos.y-size.y*.3f, size.x*.5f, size.y*.1f, size.y*.05f);
    rect(pos.x-size.x*.25f, pos.y-size.y*.3f, size.y*.1f, size.x*.55f, size.y*.05f);
    rect(pos.x+size.x*.25f-size.x*.1f, pos.y-size.y*.1f, size.x*.1f, size.x*.35f, size.y*.05f);
    rect(pos.x- size.x*.05f, pos.y+size.y*.02f, size.x*.1f, size.y*.15f, size.y*.05f);
  }

  if (icon==17) { //LOCK CLOSE
    rect(pos.x-size.x*.25f, pos.y-size.y*.1f, size.x*.5f, size.y*.1f, size.y*.05f);
    rect(pos.x-size.x*.25f, pos.y+size.y*.2f, size.x*.5f, size.y*.1f, size.y*.05f);
    rect(pos.x-size.x*.25f, pos.y-size.y*.3f, size.x*.5f, size.y*.1f, size.y*.05f);
    rect(pos.x-size.x*.25f, pos.y-size.y*.3f, size.y*.1f, size.x*.55f, size.y*.05f);
    rect(pos.x+size.x*.25f-size.x*.1f, pos.y-size.y*.3f, size.y*.1f, size.x*.55f, size.y*.05f);
    rect(pos.x- size.x*.05f, pos.y+size.y*.02f, size.x*.1f, size.y*.15f, size.y*.05f);
  }
}

public void star5(PVector pos, PVector size) {
  PVector mid = new PVector(pos.x+size.x*.5f, pos.y+size.y*.5f);
  float angle = TWO_PI / 5;
  float halfAngle = angle/2.0f;
  beginShape();
  for (float a = 0; a < TWO_PI; a += angle) {
    float sx = mid.x + cos(a) * size.x*.5f;
    float sy = mid.y + sin(a) * size.x*.5f;
    vertex(sx, sy);
    sx = mid.x + cos(a+halfAngle) * size.x*.25f;
    sy = mid.y + sin(a+halfAngle) * size.x*.25f;
    vertex(sx, sy);
  }
  endShape(CLOSE);
}

public void starsys(PVector pos, PVector size, int calif, int coff, int con) {
  for (int i=0; i<5; i++) {
    if (i<calif) fill(con);
    else fill(coff);
    star5(new PVector (pos.x+((size.x/5)*i), pos.y), new PVector (size.y, size.y));
  }
}
class Slider {
  PVector pos, size, mid, end, bupos, bupos2, busize, slsize, slstopb, slstope, clpos, clsize;
  String name;
  float value, value2, minstopv, maxstopv;
  boolean state, drag, drag2;
  float  minV=0;
  float maxV=1;
  float flt = 1;
  int type = 0;
  int icon = 0;
  float textsize = 12;
  float textdist = 90;
  float scand = 1;
  int con = 100;
  int coff = 200;
  Slider(PVector _pos, PVector _size, String _name, int _type, float _minV, float _value, float _maxV) {
    pos=_pos;
    size=_size;
    calcpos();
    nostops();
    name = _name;
    type = _type;
    minV=_minV;
    value = _value;
    value2=value;
    maxV=_maxV;
  }
  public void calcpos() {
    mid = new PVector(pos.x+size.x*.5f, pos.y+size.y*.5f);
    end = new PVector(pos.x+size.x, pos.y+size.y);
    bupos  = new PVector (map(value, minV, maxV, pos.x, end.x), mid.y);
    bupos2 = new PVector (map(value2, minV, maxV, pos.x, end.x), mid.y);
    busize = new PVector (size.y, size.y);
    slsize = new PVector (size.x, 3*scand);
    clpos = new PVector (pos.x-busize.x, pos.y);
    clsize = new PVector (busize.x, busize.y*2);
    if (type==11) {
      bupos  = new PVector (mid.x, map(value, minV, maxV, pos.y, end.y));
      bupos2 = new PVector (mid.x, map(value2, minV, maxV, pos.y, end.y));
      busize = new PVector (size.x, size.x);
      slsize = new PVector (3*scand, size.y);
      clpos = new PVector (pos.x-busize.y*.5f, pos.y-busize.y);
      clsize = new PVector (busize.x*2, busize.y);
    }
  }
  public void nostops() {
    slstopb = new PVector (pos.x, pos.y);
    slstope = new PVector (end.x, end.y);
  }
  public void scaletoandroid(float _scand) {
    scand = _scand;
    pos  = new PVector (scand*pos.x, scand*pos.y);
    size  = new PVector (scand*size.x, scand*size.y);
    calcpos();
    slstopb = new PVector (scand*slstopb.x, scand*slstopb.y);
    slstope = new PVector (scand*slstope.x, scand*slstope.y);
    textsize = textsize * scand;
  }
  public void addstops(float _minstopV, float _maxstopV) {
    minstopv = _minstopV;
    maxstopv = _maxstopV;
    slstopb = new PVector (map(minstopv, minV, maxV, pos.x, end.x), map(minstopv, minV, maxV, pos.y, end.y));
    slstope = new PVector (map(maxstopv, minV, maxV, pos.x, end.x), map(maxstopv, minV, maxV, pos.y, end.y));
  }
  public void addsecond(float _value2) {
    value2 = _value2;
    calcpos();
  }
  public void display() {
    pushStyle();
    rectMode(CORNER);
    textSize(textsize);
    if (type==10) {
      value = rg(map(bupos.x, pos.x, end.x, minV, maxV), flt); 
      if (drag) bupos.x = constrain(mouseX, slstopb.x, slstope.x);
      println(pos.x);
      if (slstopb!=null)if (slstopb.x!=pos.x) line(slstopb.x, mid.y-(busize.y*.2f), slstopb.x, mid.y+(busize.y*.2f));
      if (slstope!=null)if (slstope.x!=end.x) line(slstope.x, mid.y-(busize.y*.2f), slstope.x, mid.y+(busize.y*.2f));
      noStroke();
      fill(coff);
      rect(pos.x, mid.y-slsize.y*.5f, slsize.x, slsize.y, slsize.y*.5f); 
      fill(con);
      rect(pos.x, mid.y-slsize.y*.5f, bupos.x-pos.x, slsize.y, slsize.y/2); 
      strokeWeight(1*scand);
      if (isover()) strokeWeight(2*scand);
      stroke(con);
      fill(255);
      ellipse(bupos.x, mid.y, busize.x, busize.y);
      fill(con);
      textAlign(CENTER, CENTER);
      if (flt>1) text( PApplet.parseInt(value), bupos.x, bupos.y-busize.y*.5f-textsize*.8f);
      else text( nfc(value, 2), bupos.x, bupos.y-busize.y*.5f-textsize*.8f); 
      fill(con);
      textAlign(LEFT, CENTER);
      text(name, pos.x-textdist, mid.y);
      strokeWeight(1*scand);
    }     
    if (type==11) {                                   //Simple vertical sin nombre
      value = rg(map(bupos.y, pos.y, end.y, minV, maxV), flt); 
      if (drag) bupos.y = constrain(mouseY, slstopb.y, slstope.y);
      fill(255);
      stroke(coff);

      if (state) {
        rect(clpos.x, clpos.y, clsize.x, size.y+clsize.y, busize.y/4);
        if (slstopb!=null)if (slstopb.y!=pos.y) line(mid.x-busize.x*.2f, slstopb.y, mid.x+busize.x*.2f, slstopb.y);
        if (slstope!=null)if (slstope.y!=end.y) line(mid.x-busize.x*.2f, slstope.y, mid.x+busize.x*.2f, slstope.y);
        noStroke();
        fill(coff);
        rect( mid.x-slsize.x*.5f, pos.y, slsize.x, slsize.y, slsize.x*.5f); 
        fill(con);
        rect( mid.x-slsize.x*.5f, pos.y, slsize.x, bupos.y-pos.y, slsize.x/2); 
        strokeWeight(1*scand);
        if (isover()) strokeWeight(2*scand);
        stroke(con);
        fill(255);
        ellipse(bupos.x, bupos.y, busize.x, busize.y);
        stroke(coff);
      }
      strokeWeight(1*scand);
      if (isovercl()) strokeWeight(2*scand);
      if (!state) {
        noStroke();
        rect(clpos.x, clpos.y, busize.x*2, busize.y, busize.y/4);
        stroke(con);
        line(clpos.x+busize.x, clpos.y+busize.y-scand*5, clpos.x+busize.x-scand*5, clpos.y+busize.y-scand*10);
        line(clpos.x+busize.x, clpos.y+busize.y-scand*5, clpos.x+busize.x+scand*5, clpos.y+busize.y-scand*10);
      }
      if (state) {
        line(clpos.x+busize.x, clpos.y+busize.y-scand*10, clpos.x+busize.x-scand*5, clpos.y+busize.y-scand*5);
        line(clpos.x+busize.x, clpos.y+busize.y-scand*10, clpos.x+busize.x+scand*5, clpos.y+busize.y-scand*5);
      }
      fill(con);
      textAlign(CENTER, CENTER);

      if (flt>=1) text( PApplet.parseInt(value), mid.x, pos.y-busize.y*.7f);
      else text( nfc(value, 2), mid.x, pos.y-busize.y*.7f); 
      fill(con);
      textAlign(CENTER, CENTER);
      //text(name,  mid.x,pos.y-textsize*2);
      strokeWeight(1*scand);
    }     







    if (type==20) {
      value = rg(map(bupos.x, pos.x, end.x, minV, maxV), flt);
      value2 = rg(map(bupos2.x, pos.x, end.x, minV, maxV), flt);

      if (drag) bupos.x = constrain(mouseX, slstopb.x, bupos2.x-busize.y);
      if (drag2) bupos2.x = constrain(mouseX, bupos.x+busize.y, slstope.x);
      if (slstopb!=null)if (slstopb.x!=pos.x) line(slstopb.x, slstopb.y-(busize.y*.2f), slstopb.x, slstopb.y+slsize.y+(busize.y*.2f));
      if (slstope!=null)if (slstope.x!=end.x) line(slstope.x, slstope.y-slsize.y-(busize.y*.2f), slstope.x, slstope.y+(busize.y*.2f));
      noStroke();
      fill(coff);
      rect(pos.x, mid.y-slsize.y*.5f, slsize.x, slsize.y, slsize.y*.5f); 
      fill(con);
      rect(bupos.x, mid.y-slsize.y*.5f, bupos2.x-bupos.x, slsize.y, slsize.y/2); 
      strokeWeight(1*scand);
      stroke(con);
      fill(255);
      if (isover()) strokeWeight(2*scand);
      else  strokeWeight(1*scand);
      ellipse(bupos.x, mid.y, busize.x, busize.y);
      if (isover2()) strokeWeight(2*scand);
      else  strokeWeight(1*scand);
      ellipse(bupos2.x, mid.y, busize.x, busize.y);
      fill(con);
      textAlign(CENTER, CENTER);
      if (flt>1) text(PApplet.parseInt(value), bupos.x, pos.y-busize.y*.5f-textsize*.8f);
      else text( nfc(value, 2), bupos.x, pos.y-busize.y*.5f-textsize*.8f); 
      if (flt>1) text( PApplet.parseInt(value2), bupos2.x, pos.y-busize.y*.5f-textsize*.8f);
      else text( nfc(value2, 2), bupos2.x, pos.y-busize.y*.5f-textsize*.8f); 
      fill(con);
      textAlign(LEFT, CENTER);
      text(name, pos.x-textdist, mid.y);
      strokeWeight(1*scand);
    }









    popStyle();
  }
  public boolean isover() {
    return (mouseX>bupos.x-busize.x*.5f&&mouseX<bupos.x+busize.x*.5f&&mouseY>bupos.y-busize.y*.5f&&mouseY<bupos.y+busize.y*.5f);
  }
  public boolean isover2() {
    return (mouseX>bupos2.x-busize.x*.5f&&mouseX<bupos2.x+busize.x*.5f&&mouseY>bupos.y-busize.y*.5f&&mouseY<bupos.y+busize.y*.5f);
  }
  public boolean isovercl() {
    return (mouseX>clpos.x&&mouseX<clpos.x+clsize.x&&mouseY>clpos.y&&mouseY<clpos.y+clsize.y);
  }
  public boolean otherselectlist(ArrayList <Slider> allsl) {
    boolean bout=false;
    for (Slider o : allsl) if (o.state) bout = true;
    if (state) bout = false;
    ;
    return bout;
  }
  public boolean otherselectarr(Slider [] allsl) {
    boolean bout=false;
    for (Slider o : allsl) if (o.state) bout = true;
    if (state) bout = false;
    ;
    return bout;
  }  
  public void press() {
    if (type==11) {
      if (isovercl()) state = !state;
      if (state) {
        if (isover()) drag = true;
        if (isover2()) drag2 = true;
      }
    }
    if (type!=11) {
      if (isover()) drag = true;
      if (isover2()) drag2 = true;
    }
  }
  public void release() {
    drag = false;
    drag2 = false;
  }
  public float roundit(float numin, int dec) {                                              
    float dec10 = pow(10, dec);
    float roundout = round(numin * dec10)/dec10;
    return roundout;
  }
  public float rg(float fin, float grunit) {
    float fout = fin + grunit*.5f;
    fout = roundit(fout-(fout%grunit), 2);
    return fout;
  }
}
class Text {
  PVector pos, size, mid, end;
  String name;
  boolean state;
  String itext = "";
  String dash = "|";
  int type = 0;
  int icon = 0;
  float textsize = 12;
  float scand = 1;
  int con = 200;
  int coff = 100;
  Text(PVector _pos, PVector _size, String _name, int _type) {
    pos=_pos;
    size=_size;
    calcpos();
    name = _name;
    type = _type;
  }
  public void scaletoandroid(float _scand) {
    scand = _scand;
    pos = new PVector (pos.x*scand, pos.y*scand);
    size = new PVector (size.x*scand, size.y*scand);
    calcpos();
    textsize  = textsize*scand;
  }
  public void calcpos() {
    mid = new PVector(pos.x+size.x*.5f, pos.y+size.y*.5f);
    end = new PVector(pos.x+size.x, pos.y+size.y);
  }
  public void display() {
    textSize(textsize);
    rectMode(CORNER);
    textAlign(CENTER, CENTER);
    fill(coff);
    stroke(con);
    strokeWeight(1*scand);
    //rect(pos.x,pos.y,size.x,size.y);
    if (isover()||state) fill(con);
    if (isover()||state) stroke(con);
    if (isover()||state) strokeWeight(2*scand);
    line(pos.x, pos.y+size.y, pos.x+size.x, pos.y+size.y);
    if (itext.length()==0&&!state) text(name, mid.x, mid.y-textsize*.3f);
    if (type==10&&itext.length()<50) {
      if (state) text(itext+dash, mid.x, mid.y-textsize*.3f);
      else text(itext, mid.x, mid.y-textsize*.3f);
    }
    if (type==20&&itext.length()<50) {
      String textp = "";
      for (int i=0; i<itext.length(); i++) textp = textp + "*";
      if (state) text(textp+dash, mid.x, mid.y-textsize*.3f);
      else text(textp, mid.x, mid.y-textsize*.3f);
    }
  }
  public boolean isover() {
    if (mouseX>pos.x&&mouseX<end.x&&mouseY>pos.y&&mouseY<end.y) return true;
    else return false;
  }
  public void presson() {
    if (isover()) state = !state;
  }
  public void pressoff() {
    if (state)if (!isover()) state = false;
  }
  public void type() {
    if (state) {
      if ((key >= 'A' && key <= 'z') ||(key >= '0' && key <= '9') || key == ' '|| key=='('|| key==')'|| key==','|| key=='.'|| key=='|'||key=='@'||key=='-'||key=='_')   itext = itext + str(key);
      if ((key == CODED&&keyCode == LEFT)||keyCode == BACKSPACE)  if (itext.length()>0) itext = itext.substring(0, itext.length()-1);
    }
  }
}

public boolean textis(ArrayList <Text> alltx) {
  boolean bout = false;
  for (Text a : alltx) if (a.state) bout = true;
  return bout;
}
class Textrect {
  PVector pos, size;
  String text;
  float scand = 1;
  float textsize = 12;
  int i;
  Textrect(PVector _pos, PVector _size, String _text, int _i) {
    pos=_pos;
    size=_size;
    text=_text;
    i=_i;
  }
  public void scaletoandroid(float _scand) {
    scand = _scand;
    pos = pos.copy().mult(scand);
    size = size.copy().mult(scand);
    textsize = textsize*scand;
  }
  public void display() {
    pushStyle();
    textSize(textsize);
    fill(255);
    textAlign(CENTER, CENTER);
    //rect(pos.x, pos.y, size.x, size.y);
    fill(100);
    if (text!=null) text(text, pos.x + size.x*.5f, pos.y + size.y*.5f);
    popStyle();
  }
}
Slider sl [][];
Option en [];
Textrect tx [], tti [];
Toggle lk [];
Toggle dr;
Click evo;
Option addrooms[];
PVector uipos = new PVector (50, 10);
PVector uisize = new PVector (70, 30);
int slcolumns[][] = {{2, 3, 4}, {5, 6, 7}};

int frcount = 0;
int frcoutstop = 10;
boolean frcountrun = false;

public void recalctree() {
  setuptr();
  setupid();
  setupdraw();
  setupga();
}

public void recalchouse() {
  changeroomDATA();
  setupid();
  setupdraw();
}
public void reinserthouse() {
  roomDATA=getroomDATA(rooms, roomDATAIN);
  lockgenes = getroomLOCKS(rooms, lockIN);
  setupsl(rooms, roomDATA);
  setupho();
  setuptr();
  setupid();
  setupdraw();
  setupga();
}
public void evolveon() {
  setupga();
  evolve = true;
}
public void evolveoff() {
  if (frameCount%90==0) evolve = false;
}

public void setupsl(String [] rooms, String [][] roomDATA) {
  String ttis [] = {"", "Area", "Width", "Entry"};
  tti = new Textrect [ttis.length];
  for (int i=0; i<tti.length; i++) tti[i] = new Textrect (new PVector (uipos.x+i*uisize.x, uipos.y), uisize, ttis[i], 10);
  for (int i=0; i<tti.length; i++) tti[i].scaletoandroid(scand);
  lk = new Toggle [rooms.length];  
  for (int l=0; l<lk.length; l++)   lk[l]= new Toggle(new PVector (uipos.x-uisize.x*.5f, uipos.y+l*uisize.y+uisize.y), new PVector (uisize.y, uisize.y), "candado" + l, 50);
  for (int l=0; l<lk.length; l++)   lk[l].scaletoandroid(scand);
  tx = new Textrect [rooms.length];
  for (int i=0; i<tx.length; i++) tx[i] = new Textrect (new PVector (uipos.x, uipos.y+uisize.y+uisize.y*i), new PVector (uisize.x, uisize.y), roomideals(rooms[i], 1, roomDATA), 10);
  for (int i=0; i<tx.length; i++) tx[i].scaletoandroid(scand);
  sl = new Slider [2][rooms.length];
  for (int s=0; s<sl.length; s++) for (int i=0; i<sl[s].length; i++)  sl[s][i] = new Slider(new PVector (uipos.x+s*uisize.x+uisize.x*1.25f, uipos.y+uisize.y*2.1f+uisize.y*i), new PVector (uisize.x*.45f, 200), roomideals(rooms[i], 0, roomDATA), 11, 0, PApplet.parseFloat(roomideals(rooms[i], slcolumns[s][0], roomDATA)), min0max1roomDATA(slcolumns[s][2], roomDATA, 1));
  for (int s=0; s<sl.length; s++) for (int i=0; i<sl[s].length; i++)  sl[s][i].flt = .8f;
  for (int s=0; s<sl.length; s++) for (int i=0; i<sl[s].length; i++) sl[s][i].addstops(PApplet.parseFloat(roomideals(rooms[i], slcolumns[s][1], roomDATA)), PApplet.parseFloat(roomideals(rooms[i], slcolumns[s][2], roomDATA)));
  for (int s=0; s<sl.length; s++) for (int i=0; i<sl[s].length; i++) sl[s][i].scaletoandroid(scand);
  en = new Option [rooms.length];
  for (int l=0; l<en.length; l++) en[l] = new Option(new PVector (uipos.x+2*uisize.x+uisize.x, uipos.y+l*uisize.y+uisize.y+5), new PVector (uisize.x*1, uisize.y), roomsSINroom(rooms, l), 12); 
  for (int l=0; l<en.length; l++) en[l].namei = roomidealadj(rooms, rooms[l], roomDATA, 9); //buscar room seleccionado
  for (int l=0; l<en.length; l++) en[l].calcpos();
  for (int l=0; l<en.length; l++) en[l].scaletoandroid(scand);  
  addrooms = new Option [2];
  addrooms[0] = new Option(new PVector (30, height/scand-80), new PVector (30, 30), roomcodedeDATAIN(roomDATAIN), 13);
  addrooms[1] = new Option(new PVector (30, height/scand-40), new PVector (30, 30), rooms, 13);
  addrooms[0].icon = 2;
  addrooms[1].icon = 9;
  for (int l=0; l<addrooms.length; l++) addrooms[l].con = 150;
  for (int l=0; l<addrooms.length; l++) addrooms[l].scaletoandroid(scand);
  evo = new Click(new PVector (width*.5f/scand-uisize.x, height/scand-uisize.y*1.5f), new PVector (uisize.x, uisize.y), "design", 20);
  evo.scaletoandroid(scand);
  dr = new Toggle(new PVector (width/scand-uisize.x, height/scand-uisize.y*2), new PVector (uisize.y, uisize.y), "draw perimeter", 20);
  dr.icon = 6;
  dr.scaletoandroid(scand);
}
public void drawsl() {
  for (int i=0; i<tti.length; i++) tti[i].display();
  for (int l=0; l<lk.length; l++)  lk[l].display();
  for (int i=0; i<tx.length; i++) tx[i].display();
  for (int s=0; s<sl.length; s++) for (int i=0; i<sl[s].length; i++) sl[s][i].display();
  for (int s=0; s<sl.length; s++) for (int i=0; i<sl[s].length; i++) if (!sl[s][i].otherselectarr(sl[s])) sl[s][i].display();
  for (int s=0; s<sl.length; s++) for (int i=0; i<sl[s].length; i++) if (sl[s][i].drag) recalchouse();// changeroomDATA();
  for (int i=0; i<en.length; i++) en[i].display();
  for (int i=0; i<en.length; i++) if (!en[i].otherselectarr(en)) en[i].display();
  for (int a=0; a<addrooms.length; a++) addrooms[a].display();
  evo.display();
  evolveoff();
  dr.display();
  //for (int s=0; s<roomDATA.length; s++) for (int i=0; i<roomDATA[s].length; i++) text(roomDATA[s][i],300+i*20,30+s*20);
}
public void presssl() {
  for (int s=0; s<sl.length; s++) for (int i=0; i<sl[s].length; i++) if (!sl[s][i].otherselectarr(sl[s])) sl[s][i].press();                   //slider press
  for (int i=en.length-1; i>=0; i--) if (!en[i].otherselectarr(en)) en[i].press();
  for (int i=en.length-1; i>=0; i--) if (!en[i].state) for (int j=0; j<en[i].names.length; j++) if (en[i].isoveri()[j]) recalchouse();
  for (int l=0; l<lk.length; l++) lk[l].press();
  for (int l=0; l<lk.length; l++) if (lk[l].isover()) lockpress(rooms); 
  evo.presson();
  if (evo.state) evolveon();
  evo.pressoff();
  dr.press();
}

public void lockpress(String [] rooms) {
  boolean onelockselect = false;
  for (int l=0; l<lk.length; l++) if (lk[l].state) onelockselect = true; 
  int pmin = getngnum("pmin", rooms.length);
  int pmax = getngnum("pmax", rooms.length);
  int lmin = getngnum("lmin", rooms.length);
  int lmax = getngnum("lmax", rooms.length);
  int smin = getngnum("smin", rooms.length);
  int smax = getngnum("smax", rooms.length);
  if (!onelockselect)  for (int i=pmin; i<pmax; i++) lockgenes[i] = 0;
  if (!onelockselect)  for (int i=lmin; i<lmax; i++) lockgenes[i] = 0;
  if (onelockselect)   for (int i=pmin; i<pmax; i++) lockgenes[i] = 1;
  if (onelockselect)   for (int i=lmin; i<lmax; i++) lockgenes[i] = 1;
  for (int i=smin; i<smax; i++) lockgenes[i] = 0;
  for (int l=0; l<lk.length; l++) if (lk[l].state) {
    for (int n=0; n<ho.no.size(); n++) if (txequal(ho.no.get(n).code, rooms[l])) {
      for (int i=smin; i<smax; i++) {
        if (ho.ng[i].loc.length()<ho.no.get(n).loc.length()) if (txequal(ho.ng[i].loc, ho.no.get(n).loc.substring(0, ho.ng[i].loc.length()))) lockgenes[i] = 1;
     
    }
    }
  }
  
  
  recalctree();
}
public void pressadddrooms() {
  for (int a=0; a<addrooms.length; a++) addrooms[a].press(); 
  for (int a=0; a<addrooms.length; a++) if (addrooms[a].namei!=-1) {
    if (addrooms[0].namei!=-1) rooms = addtostarr(rooms, roomDATAIN[addrooms[0].namei][0]);
    if (addrooms[1].namei!=-1) rooms = subtostarr(rooms, rooms[addrooms[1].namei]);
    reinserthouse();
  }
  for (int a=0; a<addrooms.length; a++) addrooms[a].pressoff();
}
public void releasesl() {
  for (int s=0; s<sl.length; s++) for (int i=0; i<sl[s].length; i++) if (!sl[s][i].otherselectarr(sl[s])) sl[s][i].release();
}
public void changeroomDATA() {
  for (int s=0; s<sl.length; s++) for (int i=0; i<sl[s].length; i++) roomDATA [i][slcolumns[s][0]] = sl[s][i].value+"";
  for (int i=0; i<en.length; i++) roomDATA [i][9] = en[i].names[en[i].namei];
  grscaleDEhouse(ho);
}
  public void settings() {  fullScreen(); }
}
