package se.mitucha.showtracker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class TextProgressBar extends ProgressBar
{
  private static final float TEXT_DIP = 9.0F;
  private int aired = 200;
  private Paint airedPaint;
  final float scale = getContext().getResources().getDisplayMetrics().density;
  private int seen = 110;
  final float textSize = (int)(0.5F + 9.0F * this.scale);
  private int unaired = 10;
  private Paint unairedPaint;

  public TextProgressBar(Context paramContext)
  {
    super(paramContext);
    initializeText();
  }

  public TextProgressBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initializeText();
  }

  public TextProgressBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    initializeText();
  }

  private void initializeText()
  {
    this.airedPaint = new Paint();
    this.airedPaint.setColor(Color.parseColor("#000000"));
    this.airedPaint.setTextSize(this.textSize);
    this.airedPaint.setAntiAlias(true);
    this.unairedPaint = new Paint();
    this.unairedPaint.setColor(Color.parseColor("#000000"));
    this.unairedPaint.setTextSize(this.textSize);
    this.unairedPaint.setAntiAlias(true);
  }

  protected void onDraw(Canvas paramCanvas)
  {
    int i = 0;

      super.onDraw(paramCanvas);
      Rect localRect1 = new Rect();
      Rect localRect2 = new Rect();
      String str1 = this.seen + " of " + this.aired + " seen.";
      String str2 = " " + this.unaired + " unaired";
      this.airedPaint.getTextBounds(str1, 0, str1.length(), localRect1);
      this.unairedPaint.getTextBounds(str2, 0, str2.length(), localRect2);
      if (this.unaired > 0)
        i = localRect2.width() / 2;
      int j = getWidth() / 2 - (i + localRect1.width() / 2) - 2;
      int k = -1 + (getHeight() / 2 - localRect1.centerY());
      paramCanvas.drawText(str1, j, k, this.airedPaint);
      if (this.unaired > 0)
        paramCanvas.drawText(str2, 2 + (getWidth() / 2 + (localRect1.width() / 2 - localRect2.width() / 2)), k, this.unairedPaint);

      return;


  }

  public void setAired(int paramInt)
  {
    this.aired = paramInt;
    drawableStateChanged();
  }

  public void setSeen(int paramInt)
  {
    this.seen = paramInt;
    drawableStateChanged();
  }

  public void setUnaired(int paramInt)
  {
    this.unaired = paramInt;
    drawableStateChanged();
  }
}
