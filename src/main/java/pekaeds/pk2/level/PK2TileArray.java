package pekaeds.pk2.level;

import java.awt.Rectangle;

public class PK2TileArray {
    private int mWidth;
    private int mHeight;

    private int[] mArray;

    public PK2TileArray(int width, int height){
        this.mWidth = width;
        this.mHeight = height;

        this.mArray = new int[width*height];

        for(int i=0;i<this.mArray.length;++i){
            this.mArray[i] = 255;
        }
    }

    public int getWidth(){
        return this.mWidth;
    }

    public int getHeight(){
        return this.mHeight;
    }

    public int get(int posX, int posY){
        return this.mArray[ this.mWidth * posY + posX];
    }

    public void set(int posX, int posY, int value){
        this.mArray[ this.mWidth * posY + posX] = value;
    }

    public int getByIndex(int index){
        return this.mArray[index];
    }

    public void setByIndex(int index, int value){
        this.mArray[index] = value;
    }

    public int size(){
        return this.mArray.length;
    }

    public void removeID(int id){
        for(int i=0;i<this.mArray.length;++i){
            if(this.mArray[i]==255)continue;
            else if(this.mArray[i]==id){
                this.mArray[i]=255;
            }
            else if(this.mArray[i]>id){
                this.mArray[i]-=1;
            }
        }
    }

    public Rectangle calculateOffsets(){
        int startX = this.mWidth;
        int width = 0;
        int startY =  this.mHeight;
        int height = 0;
        
        for (int y = 0; y < this.mHeight; y++) {

            for (int x = 0; x < this.mWidth; x++) {
                
                if (this.get(x, y) != 255) {
                    if (x < startX) {
                        startX = x;
                    }
        
                    if (y < startY) {
                        startY = y;
                    }
        
                    if (x > width) {
                        width = x;
                    }
        
                    if (y > height) {
                        height = y;
                    }
                }
            }
        }
    
        if (width < startX || height < startY) {
            startX = 0;
            startY = 0;
            
            height = 1;
            width = 1;
        }
        
        return new Rectangle(startX, startY, width - startX, height - startY);
    }

}
