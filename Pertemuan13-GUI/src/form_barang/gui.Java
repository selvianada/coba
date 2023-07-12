# DOLLA
# Import Library
import streamlit as st  #mengimport library streamlit untuk membuat web apps 
import pandas as pd     #library pandas digunakan untuk manipulasi dan analisis data, pd sebagai alias untuk memanggil fungsi dan metode 
from PIL import Image   #library PIL untuk menampilkan gambar pada web apps
from sklearn.naive_bayes import GaussianNB      #library sklearn untuk membuat model Gaussian Naive Bayes  
from sklearn.preprocessing import OneHotEncoder #library sklearn untuk melakukan one-hot encoding pada variabel kategorikal 

# Header
st.write(""" 
## Aplikasi untuk Memprediksi Status HIV
""")

# Foto
img = Image.open('gambar.png') # Memanggil gambar dari folder
st.image(img)                # Menampilkan gambar dengan ukuran sesuai dengan kolom/gambar aslinya

# SELVI
hiv_data = pd.read_csv('HIV_dataset.csv', delimiter=';')  # Import dataset
hiv = pd.DataFrame(hiv_data)                              # Merubah dataset menjadi dataframe
encoder = OneHotEncoder()                                 # Memanggil fungsi OneHotEncoder
x = encoder.fit_transform(hiv.iloc[:, :-1]).toarray()     # Encode atribut(fitur) dari nominal menjadi numerik dengan OneHotEncoder lalu ditampung divariabel x
y = hiv.iloc[:, -1]                                       # Menampung atribut target pada variabel y 
model = GaussianNB()                                      # Memanggil fungsi Gaussian Naive Bayes
model.fit(x, y)                                          # Melakukan training model dengan data x dan y 


# Fungsi input_user dengan parameter i sebagai kunci masukkan dan tipe sebagai tipe masukan
# berfungsi untuk menampilkan form pertanyaan yang berkaitan dengan HIV
# dan jawaban akan dikembalikan dalam bentuk dataframe untuk di prediksi
def input_user(i, tipe) : #i sebagai kunci masukkan dan tipe sebagai tipe masukan
  if tipe != 'single' :
    st.write(f"# Data {i+1}") # i+1 karena index dimulai dari 0
  
  st.write("Silahkan mengisi beberapa pertanyaan berikut")
  col1, col2 = st.columns(2) # Membagi form menjadi 2 kolom
  
  with col1: # Kolom pertama
    marital_status = st.selectbox('Apa status pernikahan anda?',('-','Belum Menikah', 'Sudah Menikah'), key = ('marital_status', i))
    std = st.selectbox('Apakah anda memiliki penyakit kelamin?',('-', 'Tidak','Ya'), key = ('std', i))
    hiv_test_PastYear = st.selectbox('Apakah anda pernah melakukan tes HIV satu tahun terakhir?',('-', 'Tidak','Ya'), key = ('hiv_test_PastYear', i))
  with col2: # Kolom kedua
    aids_education = st.selectbox('Apakah anda mempunyai pengetahuan tentang AIDS?',('-', 'Tidak','Ya'), key = ('aids_education', i))
    drug_taking = st.selectbox('Apakah anda mengonsumsi narkoba?',('-', 'Tidak','Ya'), key = ('drug_taking', i))
  
  # Membuat dataframe dari hasil jawaban pengguna untuk diprediksi
  # data dijadikan array agar dapat diubah menjadi dataframe
  # jawaban ditampung ke list data dengan key sesuai dengan kolomnya
  data = { 'marital status' : cek_status(marital_status),
            'std' : cek_YaTidak(std),
            'hiv test in past year' : cek_YaTidak(hiv_test_PastYear),
            'aids education' : cek_YaTidak(aids_education),
            'drug-taking' : cek_YaTidak(drug_taking)
  }
  fitur = pd.DataFrame(data, index=[0]) # Mengubah data menjadi dataframe per baris
  return fitur # Mengembalikan dataframe


# RAHMA
# Fungsi cek_status dengan parameter input berupa masukan dari pengguna
# berfungsi untuk merubah masukan status menikah agar sesuai dengan data yang dimodelkan
def cek_status(input) :
  if input == 'Sudah Menikah' : # Jika input sama dengan Sudah Menikah
    return 'married' # Maka akan mengembalikan nilai married
  elif input == 'Belum Menikah' : # Jika input sama dengan Belum Menikah
    return 'unmarried' # Maka akan mengembalikan nilai unmarried
  else : 
    return '-' 

# Fungsi cek_YaTidak dengan parameter input berupa masukan dari pengguna
# berfungsi untuk merubah masukan Ya/Tidak agar sesuai dengan data yang dimodelkan
def cek_YaTidak(input) :
  if input == 'Ya' :
    return 'yes'
  elif input == 'Tidak' :
    return 'no'
  else :
    return '-'

# Fungsi cek dengan parameter predik berupa prediksi dari masukan pengguna
# berfungsi untuk mengembalikan nilai Positif/Negatif
def cek(predik) :
  if predik == 'positive' :
    return ":red[positif]" # Jika prediksi positif maka akan mengembalikan nilai positif dengan warna merah
  else :
    return ":green[negatif]" # Jika prediksi negatif maka akan mengembalikan nilai negatif dengan warna hijau

# Fungsi cek_predik dengan parameter predik berupa prediksi dari masukan pengguna
# berfungsi untuk menampilkan himbauan apabila hasil prediksi positif
def cek_predik(predik) :
  if predik == 'positive' :
    st.write(':warning: Segera kunjungi fasilitas kesehatan terdekat untuk melakukan tes HIV') # himbauan apabila hasil prediksi positif


# from_file dengan parameter i sebagai kunci data dan dataframe berupa dataframe yang berasal dari file
# berfungsi untuk memisahkan data berdasarkan index baris(row)
# dan akan dikembalikan dalam bentuk dataframe per baris sebagai masukan
def from_file(i, dataframe) : 
  # Membuat dataframe dari hasil jawaban pengguna untuk diprediksi
  # data dijadikan array agar dapat diubah menjadi dataframe
  # jawaban ditampung ke list data dengan key sesuai dengan kolomnya
  data = { 'marital status' : dataframe.iloc[i, 0],
          'std' : dataframe.iloc[i, 1],
          'hiv test in past year' : dataframe.iloc[i, 2],
          'aids education' : dataframe.iloc[i, 3],
          'drug-taking' : dataframe.iloc[i, 4]
  }
  fitur = pd.DataFrame(data, index=[0])
  return fitur

# MARCHEL
# Untuk membuat selectbox yang berada di sidebar untuk memilih tipe inputan
tipe_input = st.sidebar.selectbox('Pilih inputan?', ('', 'Single Input', 'Double Input', 'Multiple Input', 'File Input'))

# Apabila pengguna memilih single input maka hanya ada 1 form pertanyaan
if tipe_input == 'Single Input' :
  df = input_user(0, 'single')
  if st.button('Prediksi', key = 'prediksi') : # Jika tombol prediksi ditekan maka akan memprediksi data
    if df['marital status'][0] == '-' and df['std'][0] == '-' and df['hiv test in past year'][0] == '-' and df['aids education'][0] == '-' and df['drug-taking'][0] == '-' :  
      st.write("##### Data yang anda masukkan :red[belum lengkap], harap isi data dengan lengkap")
    elif df['std'][0] == '-' :
            st.write("##### Data yang anda masukkan :red[belum lengkap], harap isi data penyakit kelamin terlebih dahulu")
    else :
      input_encoded = encoder.transform(df).toarray() # Mengubah data menjadi array agar dapat diprediksi oleh model
      prediksi = model.predict(input_encoded)
      st.write("### Anda berkemungkinan ", cek(prediksi), " penyakit HIV")
      cek_predik(prediksi)

# Apabila pengguna memilih double input maka ada 2 form pertanyaan  
elif tipe_input == 'Double Input' :
  i = 0
  with st.expander(f"Input Data {i+1}", True): # membuat form pertanyaan yang dapat diexpand/ditutup
    df = input_user(i, 'multiple')
    if st.button('Prediksi', key = f'prediksi{i}') :
      if df['marital status'][0] == '-' or df['std'][0] == '-' or df['hiv test in past year'][0] == '-' or df['aids education'][0] == '-' or df['drug-taking'][0] == '-' :  
        st.write("##### Data yang anda masukkan :red[belum lengkap], harap isi data dengan lengkap")
      else :
        input_encoded = encoder.transform(df).toarray()
        prediksi = model.predict(input_encoded)
        st.write("### Anda berkemungkinan ", cek(prediksi), " penyakit HIV")
        cek_predik(prediksi)
  i += 1
  while (i < 2) :
    with st.expander(f"Input Data {i+1}"):
      df = input_user(i, 'multiple')
      if st.button('Prediksi', key = f'prediksi{i}') :
        if df['marital status'][0] == '-' or df['std'][0] == '-' or df['hiv test in past year'][0] == '-' or df['aids education'][0] == '-' or df['drug-taking'][0] == '-' :  
          st.write("##### Data yang anda masukkan :red[belum lengkap], harap isi data dengan lengkap")
        else :
          input_encoded = encoder.transform(df).toarray()
          prediksi = model.predict(input_encoded)
          st.write("### Anda berkemungkinan ", cek(prediksi), " penyakit HIV")
          cek_predik(prediksi)
    i += 1

# AS'AN
# Apabila pengguna memilih multiple input maka banyaknya form pertanyaan 
# sesuai dengan keinginan pengguna
elif tipe_input == 'Multiple Input' :
  jmlData = st.number_input(label='Ingin mengisi berapa data?', step=1)
  if jmlData > 0 :
    i = 0
    with st.expander(f"Input Data {i+1}", True):
      df = input_user(i, 'multiple')
      if st.button('Prediksi', key = f'prediksi{i}') :
        if df['marital status'][0] == '-' or df['std'][0] == '-' or df['hiv test in past year'][0] == '-' or df['aids education'][0] == '-' or df['drug-taking'][0] == '-' :  
          st.write("##### Data yang anda masukkan :red[belum lengkap], harap isi data dengan lengkap")
        else :
          input_encoded = encoder.transform(df).toarray()
          prediksi = model.predict(input_encoded)
          st.write("### Anda berkemungkinan ", cek(prediksi), " penyakit HIV")
          cek_predik(prediksi)
    i += 1
    while (i < jmlData) :
      with st.expander(f"Input Data {i+1}"):
        df = input_user(i, 'multiple')
        if st.button('Prediksi', key = f'prediksi{i}') :
          if df['marital status'][0] == '-' and df['std'][0] == '-' and df['hiv test in past year'][0] == '-' and df['aids education'][0] == '-' and df['drug-taking'][0] == '-' :
            st.write("##### Data yang anda masukkan :red[belum lengkap], harap isi data dengan lengkap") 
          else :
            input_encoded = encoder.transform(df).toarray()
            prediksi = model.predict(input_encoded)
            st.write("### Anda berkemungkinan ", cek(prediksi), " penyakit HIV")
            cek_predik(prediksi)
      i += 1

# Apabila pengguna memilih file input maka pengguna dapat menggungah file csv sebagai inputan
elif tipe_input == 'File Input' :
  uploaded_file = st.file_uploader("Silahkan pilih file", type=['csv'])
  if uploaded_file is not None:
    # Can be used wherever a "file-like" object is accepted:
    dataframe = pd.read_csv(uploaded_file)
    for i in range(len(dataframe)) :
      with st.expander(f"Data {i+1}"):
        df = from_file(i, dataframe)
        st.dataframe(df, use_container_width= True, hide_index =True, # Menampilkan data yang diupload pengguna dalam bentuk dataframe
                      column_config = {'marital status' : 'Status Pernikahan',
                                        'std' : 'Penyakit Kelamin',
                                        'hiv test in past year' : 'Tes HIV 1 tahun Terakhir',
                                        'aids education' : 'Pengetahuan Aids',
                                        'drug-taking' : 'Mengonsumsi Narkoba'}) 
        input_encoded = encoder.transform(df).toarray()
        prediksi = model.predict(input_encoded)
        st.write("### Anda berkemungkinan ", cek(prediksi), " penyakit HIV")
        cek_predik(prediksi)
