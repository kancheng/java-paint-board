
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.awt.geom.*;
import java.io.*;

class Point implements Serializable
{
 int x,y;
 Color col;
 int tool;
 int boarder;

 Point(int x, int y, Color col, int tool, int boarder)
 {
  this.x = x; 
  this.y = y;
  this.col = col;
  this.tool = tool;
  this.boarder = boarder;
  }
}


class paintboard extends Frame implements ActionListener,MouseMotionListener,MouseListener,ItemListener
{
 int x = -1, y = -1;
 int con = 1;
 /*畫筆大小*/
 int Econ = 5;
 /*橡皮大小*/

 int toolFlag = 0;/*toolFlag:工具標記*/
      /*toolFlag工具對應表：
      （0--畫筆）；（1--橡皮）；（2--清除）；
      （3--直線）；（4--圓）；（5--矩形）；
      */

 Color c = new Color(0,0,0); /*畫筆顏色*/
 BasicStroke size = new BasicStroke(con,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL);/*畫筆粗細*/
 Point cutflag = new Point(-1, -1, c, 6, con);/*截斷標誌*/

 Vector paintInfo = null;/*點信息向量組*/
 int n = 1;

 FileInputStream picIn = null;
 FileOutputStream picOut = null;
 
 ObjectInputStream VIn = null;
 ObjectOutputStream VOut = null;


/*工具面板--畫筆，直線，圓，矩形，多邊形,橡皮，清除*/

 Panel toolPanel;
 Button eraser, drLine,drCircle,drRect;
 Button clear ,pen;
 Choice ColChoice,SizeChoice,EraserChoice;
 Button colchooser;
 Label colorLab,sizePen,sizeEra;

 /*保存功能*/
 Button openPic,savePic;
 FileDialog openPicture,savePicture;
 

  paintboard(String s)
 { 
  super(s);
  addMouseMotionListener(this);
  addMouseListener(this);

  paintInfo = new Vector();

/*各工具按鈕及選擇項*/
  /*顏色選擇*/
  ColChoice = new Choice();
  ColChoice.add("black");
  ColChoice.add("red");
  ColChoice.add("blue");
  ColChoice.add("green");
  ColChoice.addItemListener(this);
  /*畫筆大小選擇*/
  SizeChoice = new Choice();
  SizeChoice.add("1");
  SizeChoice.add("3");  
  SizeChoice.add("5");
  SizeChoice.add("7");
  SizeChoice.add("9");
  SizeChoice.addItemListener(this);
  /*橡皮大小選擇*/
  EraserChoice = new Choice();
  EraserChoice.add("5");
  EraserChoice.add("9");
  EraserChoice.add("13");
  EraserChoice.add("17");
  EraserChoice.addItemListener(this);

  /*##################################################*/

  toolPanel = new Panel();

  clear = new Button("清除");
  eraser = new Button("橡皮");
  pen = new Button("畫筆");
  drLine = new Button("畫直線");
  drCircle = new Button("畫圓形");
  drRect = new Button("畫矩形");

  openPic = new Button("打開圖畫");
  savePic = new Button("保存圖畫");
  
  colchooser = new Button("顯示調色板");

  /*各組件事件監聽*/
  clear.addActionListener(this);
  eraser.addActionListener(this);
  pen.addActionListener(this);
  drLine.addActionListener(this);
  drCircle.addActionListener(this);
  drRect.addActionListener(this);
  openPic.addActionListener(this);
  savePic.addActionListener(this);
  colchooser.addActionListener(this);

  
  colorLab = new Label("畫筆顏色",Label.CENTER);
  sizePen = new Label("畫筆大小",Label.CENTER);
  sizeEra = new Label("橡皮大小",Label.CENTER);
  /*面板添加組件*/
  toolPanel.add(openPic);
  toolPanel.add(savePic);
  
  toolPanel.add(pen);
  toolPanel.add(drLine);
  toolPanel.add(drCircle);
  toolPanel.add(drRect);

  toolPanel.add(colorLab); toolPanel.add(ColChoice); 
  toolPanel.add(sizePen); toolPanel.add(SizeChoice);
  toolPanel.add(colchooser);

  toolPanel.add(eraser);
  toolPanel.add(sizeEra); toolPanel.add(EraserChoice);


  toolPanel.add(clear);

  /*工具面板到APPLET面板*/
  add(toolPanel,BorderLayout.NORTH);

  setBounds(60,60,900,600); setVisible(true);
  validate();
  /*dialog for save and load*/

  openPicture = new FileDialog(this,"打開圖畫",FileDialog.LOAD);
  openPicture.setVisible(false);
  savePicture = new FileDialog(this,"保存圖畫",FileDialog.SAVE);
  savePicture.setVisible(false);

  openPicture.addWindowListener(new WindowAdapter()
  {
   public void windowClosing(WindowEvent e)
   { openPicture.setVisible(false); }
  });

  savePicture.addWindowListener(new WindowAdapter()
  {
   public void windowClosing(WindowEvent e)
   { savePicture.setVisible(false); }
  });

  addWindowListener(new WindowAdapter()
  {
   public void windowClosing(WindowEvent e)
   { System.exit(0);}
  });
 
  
 }

 public void paint(Graphics g)
 {
  Graphics2D g2d = (Graphics2D)g;

  Point p1,p2;

  n = paintInfo.size();
  
  if(toolFlag==2)
    g.clearRect(0,0,getSize().width,getSize().height);/*清除*/

  for(int i=0; i<n-1; i++)
  {
   p1 = (Point)paintInfo.elementAt(i);
   p2 = (Point)paintInfo.elementAt(i+1);
   size = new BasicStroke(p1.boarder,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL);


      g2d.setColor(p1.col);
   g2d.setStroke(size);

  if(p1.tool==p2.tool)
   {
   switch(p1.tool)
   {
    case 0:/*畫筆*/

      Line2D line1 = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
      g2d.draw(line1);      
     break;

    case 1:/*橡皮*/
      g.clearRect(p1.x, p1.y, p1.boarder, p1.boarder);
     break;

    case 3:/*畫直線*/
      Line2D line2 = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
      g2d.draw(line2);
     break;

    case 4:/*畫圓*/
       Ellipse2D ellipse = new Ellipse2D.Double(p1.x, p1.y, Math.abs(p2.x-p1.x) , Math.abs(p2.y-p1.y));
       g2d.draw(ellipse);
     break;

    case 5:/*畫矩形*/
       Rectangle2D rect = new Rectangle2D.Double(p1.x, p1.y, Math.abs(p2.x-p1.x) , Math.abs(p2.y-p1.y));
       g2d.draw(rect);
     break;

    case 6:/*截斷，跳過*/
      i=i+1;
     break;

    default :
   }/*end switch*/
   }/*end if*/
  }/*end for*/
 }


 public void itemStateChanged(ItemEvent e)
 {
  if(e.getSource()==ColChoice)/*預選顏色*/
  {
   String name = ColChoice.getSelectedItem();
   
   if(name=="black")
   { c = new Color(0,0,0); }
   else if(name=="red")
   { c = new Color(255,0,0); }
   else if(name=="green")
   { c = new Color(0,255,0); }
   else if(name=="blue")
   { c = new Color(0,0,255); }
  }
  else if(e.getSource()==SizeChoice)/*畫筆大小*/
  {
   String selected = SizeChoice.getSelectedItem();
   
   if(selected=="1")
   { 
    con = 1; 
    size = new BasicStroke(con,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL);

   }
   else if(selected=="3")
   { 
    con = 3; 
    size = new BasicStroke(con,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL);

   }
   else if(selected=="5")
   { con = 5; 
    size = new BasicStroke(con,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL);

   }
   else if(selected=="7")
   { con = 7; 
    size = new BasicStroke(con,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL);

   }
   else if(selected=="9")
   { con = 9; 
    size = new BasicStroke(con,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL);

   }
  }
  else if(e.getSource()==EraserChoice)/*橡皮大小*/
  {
   String Esize = EraserChoice.getSelectedItem();

   if(Esize=="5")
   {  Econ = 5*2; }
   else if(Esize=="9")
   {  Econ = 9*2; }
   else if(Esize=="13")
   {  Econ = 13*2; }
   else if(Esize=="17")
   {  Econ = 17*3; }

  }

 }

 public void mouseDragged(MouseEvent e)
 {
  Point p1 ;
  switch(toolFlag){
   case 0:/*畫筆*/
     x = (int)e.getX(); 
     y = (int)e.getY();
     p1 = new Point(x, y, c, toolFlag, con);
     paintInfo.addElement(p1);
     repaint();
     break;

   case 1:/*橡皮*/
     x = (int)e.getX(); 
     y = (int)e.getY();
     p1 = new Point(x, y, null, toolFlag, Econ);
     paintInfo.addElement(p1);
     repaint();
     break;

   default :
  }
 }

 public void mouseMoved(MouseEvent e) {}

 public void update(Graphics g)
 {
  paint(g);
 }

 public void mousePressed(MouseEvent e) 
 { 
  Point p2;
  switch(toolFlag){
   case 3:/*直線*/
     x = (int)e.getX(); 
     y = (int)e.getY();
     p2 = new Point(x, y, c, toolFlag, con);
     paintInfo.addElement(p2);
     break;

   case 4: /*圓*/
     x = (int)e.getX(); 
     y = (int)e.getY();
     p2 = new Point(x, y, c, toolFlag, con);
     paintInfo.addElement(p2);
     break;

   case 5: /*矩形*/
     x = (int)e.getX(); 
     y = (int)e.getY();
     p2 = new Point(x, y, c, toolFlag, con);
     paintInfo.addElement(p2);
     break;

   default :
  } 
 }

 public void mouseReleased(MouseEvent e)
 { 
  Point p3; 
  switch(toolFlag){
   case 0: /*畫筆*/
     paintInfo.addElement(cutflag);
     break;

   case 1: /*eraser*/
     paintInfo.addElement(cutflag);
     break;

   case 3: /*直線*/
     x = (int)e.getX(); 
     y = (int)e.getY();
     p3 = new Point(x, y, c, toolFlag, con);
     paintInfo.addElement(p3);
     paintInfo.addElement(cutflag);
     repaint();
     break;

   case 4: /*圓*/
     x = (int)e.getX(); 
     y = (int)e.getY();
     p3 = new Point(x, y, c, toolFlag, con);
     paintInfo.addElement(p3);
     paintInfo.addElement(cutflag);
     repaint();
     break;

   case 5: /*矩形*/
     x = (int)e.getX(); 
     y = (int)e.getY();
     p3 = new Point(x, y, c, toolFlag, con);
     paintInfo.addElement(p3);
     paintInfo.addElement(cutflag);
     repaint();
     break;

   default:
  }
 }

 public void mouseEntered(MouseEvent e){}

 public void mouseExited(MouseEvent e){}

 public void mouseClicked(MouseEvent e){}

 public void actionPerformed(ActionEvent e)
 {
    
  if(e.getSource()==pen)/*畫筆*/
  { toolFlag = 0; }

  if(e.getSource()==eraser)/*橡皮*/
  { toolFlag = 1; }
  
  if(e.getSource()==clear)/*清除*/
  {
   toolFlag = 2;
   paintInfo.removeAllElements();
   repaint(); 
  }
  
  if(e.getSource()==drLine)/*畫線*/
  { toolFlag = 3; }

  if(e.getSource()==drCircle)/*畫圓*/
  { toolFlag = 4; }

  if(e.getSource()==drRect)/*畫矩形*/
  { toolFlag = 5; }

  if(e.getSource()==colchooser)/*調色板*/
  {   
   Color newColor = JColorChooser.showDialog(this,"調色板",c);
   c = newColor;   
  }

  if(e.getSource()==openPic)/*打開圖畫*/
  {
   
   openPicture.setVisible(true);   
   
   if(openPicture.getFile()!=null)
   { 
    int tempflag;
    tempflag = toolFlag;
    toolFlag = 2 ;
    repaint();

    try{ 
      paintInfo.removeAllElements();
      File filein = new File(openPicture.getDirectory(),openPicture.getFile());
      picIn = new FileInputStream(filein);
      VIn = new ObjectInputStream(picIn);
      paintInfo = (Vector)VIn.readObject();
      VIn.close();
      repaint();
      toolFlag = tempflag;

     }

    catch(ClassNotFoundException IOe2)
    {
     repaint();
     toolFlag = tempflag;
     System.out.println("can not read object");
    }
    catch(IOException IOe) 
    {
     repaint();
     toolFlag = tempflag;
     System.out.println("can not read file");
    }
   }

  }

  if(e.getSource()==savePic)/*保存圖畫*/
  {
   savePicture.setVisible(true);
   try{
     File fileout = new File(savePicture.getDirectory(),savePicture.getFile());
     picOut = new FileOutputStream(fileout);
     VOut = new ObjectOutputStream(picOut);
     VOut.writeObject(paintInfo);
     VOut.close();
    }
   catch(IOException IOe) 
    {
      System.out.println("can not write object");
    }

   
  }
 }
}/*end paintboard*/
public class pb
{
 public static void main(String args[])
 { new paintboard("畫圖程序"); }
}
