import pandas as pd
import io
import re
import os
import sys

day_map = {
    'Segunda': 'SEGUNDA',
    'Terça': 'TERCA',
    'Quarta': 'QUARTA',
    'Quinta': 'QUINTA',
    'Sexta': 'SEXTA',
    'Sábado': 'SABADO'
}

def process_file(input_filename):
    print(f"Processing: {input_filename}...")
    
    if not os.path.exists(input_filename):
        print(f"  Error: File not found.")
        return

    base_name = os.path.splitext(input_filename)[0]
    output_filename = f"{base_name}.csv"

    try:
        with open(input_filename, "r", encoding="utf-8") as f:
            html_content = f.read()
    except UnicodeDecodeError:
        with open(input_filename, "r", encoding="latin-1") as f:
            html_content = f.read()

    # ensure content is in table
    if "<table" not in html_content:
        html_content = f"<table>{html_content}</table>"

    # parse HTML
    try:
        dfs = pd.read_html(io.StringIO(html_content))
        df = dfs[0]
    except ValueError as e:
        print(f"  Error parsing HTML: {e}")
        return

    if df.empty:
        print("  Warning: Table is empty.")
        return

    df = df.replace(r'^\s*$', float('nan'), regex=True)
    df.iloc[:, 0] = df.iloc[:, 0].ffill()
    df.iloc[:, 1] = df.iloc[:, 1].ffill()

    # col map
    # 12 to match structure
    df.columns = [
        'raw_name', 'creditos', 'codigo_turma', 
        'vagas_vet', 'vagas_cal', 'raw_vagas_amp', 'raw_occ_vet', 'raw_occ_cal', 
        'raw_schedule', 'raw_professor', 'dump1', 'dump2' 
    ][:12] 

    df[['codigo_disciplina', 'nome_disciplina']] = df['raw_name'].str.extract(r'\((.*?)\)\s*(.*)')
    
    df['vagas'] = pd.to_numeric(df['vagas_vet'], errors='coerce').fillna(0) + \
                  pd.to_numeric(df['vagas_cal'], errors='coerce').fillna(0)

    # Clean Professor
    df['professor'] = df['raw_professor'].astype(str).apply(lambda x: x.split(' - ')[0] if ' - ' in x else x)

    # 5. Explode Rows by Day/Time
    new_rows = []
    
    days_regex = r"(Segunda|Terça|Quarta|Quinta|Sexta|Sábado)"
    time_regex = r"(\d{1,2}:\d{2})-(\d{1,2}:\d{2})"

    for index, row in df.iterrows():
        raw_text = str(row['raw_schedule'])
        
        matches = list(re.finditer(f"{days_regex}\\s+{time_regex}", raw_text))
        
        if not matches:
            new_rows.append({
                'codigo_disciplina': row['codigo_disciplina'],
                'nome_disciplina': row['nome_disciplina'],
                'creditos': row['creditos'],
                'codigo_turma': row['codigo_turma'],
                'professor': row['professor'],
                'sala': "N/A",
                'vagas': int(row['vagas']),
                'dia': "",
                'inicio': "",
                'fim': ""
            })
            continue

        for i, match in enumerate(matches):
            raw_day = match.group(1)
            inicio = match.group(2)
            fim = match.group(3)
            
            # normalise day
            dia = day_map.get(raw_day, raw_day.upper())
            
            # extract room
            start_index = match.end()
            end_index = matches[i+1].start() if i + 1 < len(matches) else len(raw_text)
            
            segment = raw_text[start_index:end_index]
            sala = re.sub(r'^\s*\d+\s*', '', segment).strip()
            
            new_rows.append({
                'codigo_disciplina': row['codigo_disciplina'],
                'nome_disciplina': row['nome_disciplina'],
                'creditos': row['creditos'],
                'codigo_turma': row['codigo_turma'],
                'professor': row['professor'],
                'sala': sala,
                'vagas': int(row['vagas']),
                'dia': dia,
                'inicio': inicio,
                'fim': fim
            })

    final_df = pd.DataFrame(new_rows)

    # get rid of .0s
    final_df['creditos'] = pd.to_numeric(final_df['creditos'], errors='coerce').fillna(0).astype(int)
    final_df['vagas'] = pd.to_numeric(final_df['vagas'], errors='coerce').fillna(0).astype(int)

    # reorder
    final_df = final_df[[
        'codigo_disciplina', 'nome_disciplina', 'creditos', 
        'codigo_turma', 'professor', 'sala', 
        'vagas', 'dia', 'inicio', 'fim'
    ]]

    final_df.to_csv(output_filename, index=False, encoding='utf-8-sig')
    print(f"  -> Saved to {output_filename}")

# main
if __name__ == "__main__":
    # Check if arguments were passed
    if len(sys.argv) < 2:
        print("Usage: python parser.py <file1.html> [file2.html ...]")
    else:
        # Loop through all arguments provided
        for filename in sys.argv[1:]:
            process_file(filename)
