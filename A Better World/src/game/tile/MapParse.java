package game.tile;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MapParse {

     public MapParse() {

     }

     public Map parsing(String path){
         Map buffer_map = new Map();

         try {
                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                Document document = builder.parse(getClass().getClassLoader().getResourceAsStream(path));
                document.getDocumentElement().normalize();

                String widthAttr = document.getDocumentElement().getAttribute("width");
                String heightAttr = document.getDocumentElement().getAttribute("height");
                String scaleAttr = document.getDocumentElement().getAttribute("scale");

                int width = !widthAttr.isEmpty() ? Integer.parseInt(widthAttr) : 0;
                int height = !heightAttr.isEmpty() ? Integer.parseInt(heightAttr) : 0;
                int scale = !scaleAttr.isEmpty() ? Integer.parseInt(scaleAttr) : 1;
                buffer_map.setSize(width, height);
                buffer_map.setScale(scale);

                NodeList list = document.getElementsByTagName("tileset");
                int i = 0;
                while(list != null && list.getLength() > 0) {
                    Node node = list.item(i);
                    if(node == null)break;
                    Element element = (Element) node;

                    String imagePath = element.getAttribute("name");
                    int firstGid = Integer.parseInt(element.getAttribute("firstgid"));
                    int tileWidth = Integer.parseInt(element.getAttribute("tilewidth"));
                    int tileHeight = Integer.parseInt(element.getAttribute("tileheight"));
                    int tileCount = Integer.parseInt(element.getAttribute("tilecount"));
                    int tileColumns = Integer.parseInt(element.getAttribute("columns"));

                    TileSet tileset = new TileSet(firstGid, imagePath, tileWidth, tileHeight, tileCount, tileColumns);
                    buffer_map.addTileSet(tileset);
                    i++;
                }

                if(i == 0){
                    System.err.println("No tileset found\n");
                    return null;
                }

             // Parsing the layers
            list = document.getElementsByTagName("layer");
            int layers = list.getLength();
            if (layers > 0) {
                for (i = 0; i < layers; i++) {
                    Node node = list.item(i);
                    Element element = (Element) node;
                    width = Integer.parseInt(element.getAttribute("width"));
                    height = Integer.parseInt(element.getAttribute("height"));
                    int id = Integer.parseInt(element.getAttribute("id"));
                    String name = element.getAttribute("name");
                    Layer layer = new Layer(name, width, height, id);
                    String layerData = element.getElementsByTagName("data").item(0).getTextContent();
                    layer.parseString(layerData);
                    buffer_map.addLayer(layer);
                }
            } else {
                System.out.println("Error: No layers found in XML.");
                return null;
            }
         } catch (Exception e) {
                e.printStackTrace();
         }


         return buffer_map;
     }
}
