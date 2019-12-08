package com.example.a3520096.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.*;
import android.widget.*;
import l2i013.musidroid.model.*;
import l2i013.musidroid.util.*;
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;


public class TouchActivity extends Activity implements AdapterView.OnItemSelectedListener{
    TheApplication app;
    Partition p= new Partition(100);
    static int LastPosition=0;
    static ArrayList tab_pos[]=new ArrayList[127];
    static ArrayList tab_duree[]=new ArrayList[127];
    static int col;
    static int lig;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app=(TheApplication)this.getApplication();
        setContentView(R.layout.activity_touch);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//Cache le clavier a l'arrivée dans l'application
        if(isExternalStorageWritable()) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        ImageButton button ;
        SeekBar seekBar;
        /*-----------------CHOIX DE L'OCTAVE---------------------*/
        Spinner spinnerOctave =(Spinner) findViewById(R.id.octaveChoice);
        spinnerOctave.setOnItemSelectedListener(this);
        String[] octave = new String[]{
                "Octave...","1","2","3","4","5","6","7","8",};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,octave){
            @Override
            public boolean isEnabled(int position){
                return (position != 0);
            }

            public View getDropDownView(int position, View convertView,ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0)
                    tv.setTextColor(Color.GRAY);
                else
                    tv.setTextColor(Color.BLACK);
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerOctave.setAdapter(spinnerArrayAdapter);
        /*--------------------------------------------------------*/

        /*-----------------CHOIX DE L'INSTRUMENT---------------------*/
        Spinner spinnerInstru = (Spinner) findViewById(R.id.instruChoice);
        spinnerInstru.setOnItemSelectedListener(this);

        List<InstrumentName> list = new ArrayList<>(Arrays.asList(InstrumentName.values()));

        ArrayAdapter<InstrumentName> dataAdapter2 = new ArrayAdapter<>(this,R.layout.spinner_item2,list);
        dataAdapter2.setDropDownViewResource(R.layout.spinner_dropdown_item2);
        spinnerInstru.setAdapter(dataAdapter2);
        spinnerInstru.setSelection(0);
        /*-----------------------------------------------------------*/

        /*------------------------SEEK BAR---------------------------*/
        seekBar=(SeekBar)findViewById(R.id.SeekSurface);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                app.getModel().setRelatif(progress);
                ((TouchBoard)findViewById(R.id.boardSurface)).reDraw();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        /*-----------------------------------------------------------*/

        /*--------------------SEEK BAR VERTICAL-----------------------*/
        if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
            seekBar=(SeekBar)findViewById(R.id.seeknote);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressChangedValue = 0;

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChangedValue = progress;
                    app.getModel().setVertical(progress);
                    ((TouchBoard)findViewById(R.id.boardSurface)).reDraw();
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }

        /*-----------------------------------------------------------*/

        /*----------------------EXIT BUTTON--------------------------*/
        button = (ImageButton) findViewById(R.id.exit);
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PrintText("QUIT");
                return true;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickExit(v);
            }
        });
        /*-----------------------------------------------------------*/

        /*----------------------PLAY BUTTON--------------------------*/
        button = (ImageButton) findViewById(R.id.clickPlay);
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PrintText("Jouer toute la Partition");
                return true;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPlay(v);

            }
        });
        /*-----------------------------------------------------------*/

        /*----------------------RESET BUTTON-------------------------*/
        button = (ImageButton) findViewById(R.id.Reset);
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PrintText("Reset la grille Actuelle");
                return true;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickReset(v);
            }
        });
        /*-----------------------------------------------------------*/

        /*------------------------ADD BUTTON-------------------------*/
        button = (ImageButton) findViewById(R.id.addpart);
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PrintText("Ajoute la grille a la Partition");
                return true;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAdd(v);
                PrintText("Added");
            }
        });
        /*-----------------------------------------------------------*/

        /*---------------------RESET ALL BUTTON-----------------------*/
        button = (ImageButton) findViewById(R.id.resetAll);
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PrintText("Reset toute la Partition");
                return true;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickResetAll(v);
                PrintText("Reset");
            }
        });
        /*-----------------------------------------------------------*/
        /*--------------------PLAY GRILLE BUTTON---------------------*/
        button = (ImageButton) findViewById(R.id.PlayGrille);
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PrintText("Joue la grille actuelle");
                return true;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPlayGrille(v);
            }
        });
        /*-----------------------------------------------------------*/
        /*-----------------------SAVE BUTTON-------------------------*/
        button = (ImageButton) findViewById(R.id.Save);
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PrintText("Sauvegarde la partition");
                return true;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSave(v);
                PrintText("Saved");
            }
        });
        /*-----------------------------------------------------------*/
        /*---------------------RESTORE BUTTON-----------------------*/
        button = (ImageButton) findViewById(R.id.Restore);
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PrintText("Importe la partition sauvegardée");
                return true;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRestore(v);
                PrintText("Restored");
            }
        });
        /*-----------------------------------------------------------*/
    }

    public void PrintText(String s){
        Toast.makeText(this,s,Toast.LENGTH_LONG).show();
    }

    public void onClickBulb(View v){
        PrintText("Maintenir les boutons pour plus d'infos");
    }

    public void onClickExit(View v){
        finish();
    }

    public void onClickPlay(View v){
        MediaPlayer mPlayer = new MediaPlayer();
        MediaPlayer.OnPreparedListener mPrepared = new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer playerM){ }
        };
        mPlayer.setOnPreparedListener(mPrepared);

        Application appli = (Application) getApplicationContext();
        File f = new File(appli.getFilesDir(),"Partition.mid");
        MidiFile2I013.write(f,p);

        try {
            mPlayer.setDataSource((f).getPath());
            mPlayer.setLooping(false);
            mPlayer.prepare();

            if (!mPlayer.isPlaying()) {
                mPlayer.start();}
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onClickPlayGrille(View v){
        Model m = app.getModel();
        Partition temp = new Partition(getTempo());
        col=m.getCol();
        lig=m.getLig();
        int ip1 = temp.addPart(m.getInname(),m.getOctave());
        ListIterator<Position> it=m.getAll();
        ListIterator<Integer> itd=m.getAllD();
        while(it.hasNext()){//recupere les notes de la grille
            Position tmp=it.next();
            int tmpd=itd.next();
            System.out.println("AAAA"+tmp.getY());
            System.out.println("BBBB"+(((float)tmp.getY()/lig)+0.08));
            //Petit souci pour le si pcq la div fait 10.92 au lieu de 11
            double n = (double)tmp.getY()/lig+0.08;
            System.out.println("CCC"+n);
            temp.addNote(ip1,tmp.getX()/col,NoteName.values()[(int)n],tmpd);
        }

        MediaPlayer mPlayer = new MediaPlayer();
        MediaPlayer.OnPreparedListener mPrepared = new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer playerM){ }
        };
        mPlayer.setOnPreparedListener(mPrepared);

        Application ap = (Application) getApplicationContext();
        File f = new File(ap.getFilesDir(),"tmp.mid");
        MidiFile2I013.write(f,temp);

        try {
            mPlayer.setDataSource((f).getPath());
            mPlayer.setLooping(false);
            mPlayer.prepare();

            if (!mPlayer.isPlaying()) {
                mPlayer.start();}
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickReset(View v) {
        TheApplication app = (TheApplication)getApplication();
        app.getModel().reset();
        ((TouchBoard)findViewById(R.id.boardSurface)).reDraw();
    }

    public void onClickResetAll(View v){
        p=new Partition(100);
    }

    public void onClickAdd(View v){//Ajoute la grille a la partition
        Model m=app.getModel();
        col=m.getCol();
        lig=m.getLig();
        p.setTempo(getTempo());
        ListIterator<Position> it=m.getAll();
        ListIterator<Integer> itd=m.getAllD();

        int index = p.addPart(m.getInname(),m.getOctave());

        while(it.hasNext()){
            Position tmp=it.next();
            int tmpd=itd.next();
            p.addNote(index,tmp.getX()/col,NoteName.values()[tmp.getY()/lig],tmpd);
        }
        m.reset();
        ((TouchBoard)findViewById(R.id.boardSurface)).reDraw();
    }

    public int getTempo(){//set le tempo, jamais en dessous de 40 et au dessus de 500
        Model m = app.getModel();
        EditText mEdit   = (EditText)findViewById(R.id.EditTempo);
        int Tempo;

        if ((mEdit.getText().length()==0)) app.getModel().setTempo(100);
        else{
            Tempo=Integer.parseInt(mEdit.getText().toString());
            if(Tempo<40)    m.setTempo(40);
            if(Tempo>500)   m.setTempo(500);
            else            m.setTempo(Tempo);
        }
        return m.getTempo();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TheApplication app=(TheApplication) getApplication();
        Model m = app.getModel();
        Spinner spinner = (Spinner) parent;
        if(spinner.getId()==R.id.octaveChoice) {//Choix de l'octave
            String selectedItemText = (String) parent.getItemAtPosition(position);
            if(position > 0){
                Toast.makeText(getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT).show();
            }
            m.setOctave(position);
        }
        if(spinner.getId()==R.id.instruChoice) {//Choix de l'instrument
            ArrayList<Position> temp=m.PosIteratorToArray(m.getAll());
            ArrayList<Integer> tempD=m.DureeIteratorToArray(m.getAllD());
            tab_pos[LastPosition] = temp;
            tab_duree[LastPosition] = tempD;
            m.setInstru(InstrumentName.values()[position]);
            if(tab_pos[position]!=null){
                m.setAll(tab_pos[position]);
                m.setDuree(tab_duree[position]);
                ((TouchBoard)findViewById(R.id.boardSurface)).reDraw();
                LastPosition=position;
                return ;
            }

            LastPosition=position;
            m.reset();
            ((TouchBoard)findViewById(R.id.boardSurface)).reDraw();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void saveScore(String fname) {
        try {
            DocumentBuilderFactory Factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder Builder = Factory.newDocumentBuilder();
            Document doc = Builder.newDocument();
            //----------------------Check if permissions--------------------//
            if(isExternalStorageWritable()) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            //--------------------------------------------------------------//
                File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(),fname);

                //Chemin:/storage/emulated/0/Downloads/partition.xml
                //----------------------Ecriture--------------------//
                Element elem = doc.createElement("partition");
                Attr attr = doc.createAttribute("tempo");
                attr.setValue(String.valueOf(p.getTempo()));
                elem.setAttributeNode(attr);



                for(int i=0;i<p.getSize();i++){
                    Element instru=doc.createElement("instrument_part");
                    Attr attr2 = doc.createAttribute("num");
                    attr2.setValue(String.valueOf(p.getPart(i).getInstrument().getNum()));
                    instru.setAttributeNode(attr2);

                    Attr attr3 = doc.createAttribute("octave");
                    attr3.setValue(String.valueOf(p.getPart(i).getOctave()-1));
                    instru.setAttributeNode(attr3);

                    ListIterator<Note> it=p.getPart(i).getNotes().listIterator();
                    while(it.hasNext()){
                        Element not=doc.createElement("note");
                        Note note=it.next();
                        Attr attr4 = doc.createAttribute("num");
                        attr4.setValue(String.valueOf(note.getName().getNum()));
                        not.setAttributeNode(attr4);
                        Attr attr5 = doc.createAttribute("instant");
                        attr5.setValue(String.valueOf(note.getInstant()));
                        not.setAttributeNode(attr5);
                        Attr attr6 = doc.createAttribute("duration");
                        attr6.setValue(String.valueOf(note.getDuration()));
                        not.setAttributeNode(attr6);
                        instru.appendChild(not);
                    }
                    elem.appendChild(instru);

                }
                doc.appendChild(elem);
                //--------------------------------------------------//

                //----------------CONVERSION EN XML---------------//
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(f);
                transformer.transform(source, result);
                //------------------------------------------------//
            }
        } catch (Exception e) {
            e.printStackTrace();
            //PrintText("EXCEPTION "+e.getMessage());
        }
    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }

    public Document readXMLFile(String fname) {
        try {
            File f=new File(fname);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(f);
        } catch (Exception ex) {
            return null;
        }
    }

    public void XmltoPart(String fname){
        Document doc = readXMLFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+"/"+fname);
        Element part=doc.getDocumentElement();
        p=new Partition(Integer.parseInt(part.getAttribute("tempo")));
        NodeList instru=part.getChildNodes();
        for(int i=0;i<instru.getLength();i++){//Parcours tout les instruments
            Node inst=instru.item(i);

            int ip=p.addPart(InstrumentName.values()[Integer.parseInt(((Attr)(inst.getAttributes().getNamedItem("num"))).getValue())],Integer.parseInt(((Attr)(inst.getAttributes().getNamedItem("octave"))).getValue())+1);
            NodeList notes= instru.item(i).getChildNodes();
            for(int j=0;j<notes.getLength();j++){//Parcours toutes les notes d'un instrument
                Node note=notes.item(j);
                NoteName noteName=NoteName.values()[Integer.parseInt(((Attr)(note.getAttributes().getNamedItem("num"))).getValue())];
                int instant = Integer.parseInt(((Attr)(note.getAttributes().getNamedItem("instant"))).getValue());
                int duree = Integer.parseInt(((Attr)(note.getAttributes().getNamedItem("duration"))).getValue());
                p.addNote(ip,instant,noteName,duree);
            }
        }
        ((TouchBoard)findViewById(R.id.boardSurface)).reDraw();
    }

    public void onClickSave (View v){
        saveScore("partition.xml");
    }

    public void onClickRestore (View v){
        XmltoPart("partition.xml");
    }
}